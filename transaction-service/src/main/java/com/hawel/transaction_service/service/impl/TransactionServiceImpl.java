package com.hawel.transaction_service.service.impl;

import com.hawel.common_service.dto.ledger.*;
import com.hawel.common_service.dto.wallet.WalletTransferValidationRequest;
import com.hawel.common_service.dto.wallet.WalletTransferValidationResponse;
import com.hawel.common_service.enums.JournalStatus;
import com.hawel.transaction_service.client.wallet.LedgerClient;
import com.hawel.transaction_service.client.wallet.WalletClient;
import com.hawel.transaction_service.dto.request.TransferTransactionRequest;
import com.hawel.transaction_service.dto.response.TransactionResponse;
import com.hawel.transaction_service.entity.Transaction;
import com.hawel.transaction_service.enums.TransactionStatus;
import com.hawel.transaction_service.enums.TransactionType;
import com.hawel.transaction_service.exception.IdempotencyConflictException;
import com.hawel.transaction_service.exception.TransactionException;
import com.hawel.transaction_service.repository.IdempotencyKeyRepository;
import com.hawel.transaction_service.repository.TransactionRepository;
import com.hawel.transaction_service.service.RequestHashService;
import com.hawel.transaction_service.service.TransactionCreationService;
import com.hawel.transaction_service.service.TransactionService;
import com.hawel.transaction_service.service.TransactionStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    private final RequestHashService requestHashService;

    private final TransactionReferenceGeneratorImpl transactionReferenceGenerator;

    private final WalletClient walletClient;

    private final LedgerClient ledgerClient;

    private final TransactionStateService transactionStateService;

    private final TransactionCreationService transactionCreationService;

    @Override
    public TransactionResponse transfer(TransferTransactionRequest request, String idempotencyKey) {


        log.info(
                "Starting transfer | idempotencyKey={} | senderWalletId={} | receiverWalletId={} | amount={} | currency={}",
                idempotencyKey,
                request.getSenderWalletId(),
                request.getReceiverWalletId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        // 1. Generate request hash
        String requestHash = requestHashService.generateHash(request);

        log.debug("Request hash generated | idempotencyKey={} | requestHash={}", idempotencyKey, requestHash);

        // 2. Check idempotency
        Transaction existing = checkIdempotency(idempotencyKey, requestHash);

        if (existing != null) {

            log.info("Idempotent request detected | idempotencyKey={} | transactionId={} | status={}",
                    idempotencyKey,
                    existing.getId(),
                    existing.getStatus()
            );

            return mapToResponse(existing);
        }

        log.debug("No existing transaction found for idempotency key | idempotencyKey={}", idempotencyKey);

        // 3. validate request is not same wallet and amount is greater than zero
        validateTransfer(request);

        log.debug("Transfer request validation successful | senderWalletId={} | receiverWalletId={}", request.getSenderWalletId(), request.getReceiverWalletId());

        // 4. Build transaction
        Transaction transaction = buildTransaction(request);

        log.debug(
                "Transaction object built | transactionId={} | referenceNumber={} | status={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                transaction.getStatus()
        );

        /*
         * 5. Atomic creation
         *
         * Transaction
         * + PENDING history
         * + Idempotency key
         */
        transaction = transactionCreationService.create(transaction, idempotencyKey, requestHash);

        log.info(
                "Transaction created successfully | transactionId={} | referenceNumber={} | status={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                transaction.getStatus()
        );


        // 6. Change status to VALIDATING

        log.info(
                "Starting wallet validation | transactionId={} | referenceNumber={}",
                transaction.getId(),
                transaction.getReferenceNumber()
        );

        transactionStateService.changeStatus(transaction, TransactionStatus.VALIDATING, "Wallet validation started");

        // 7. Validate wallets by calling wallet service

        log.debug(
                "Calling wallet-service for transfer validation | transactionId={} | senderWalletId={} | receiverWalletId={} | amount={}",
                transaction.getId(),
                transaction.getSenderWalletId(),
                transaction.getReceiverWalletId(),
                transaction.getAmount()
        );

        WalletTransferValidationResponse walletResult = validateWallets(transaction);

        // Wallet Rejection, change status to FAILED and return response
        if (!walletResult.isValid()) {

            log.warn(
                    "Wallet validation failed | transactionId={} | referenceNumber={} | reason={}",
                    transaction.getId(),
                    transaction.getReferenceNumber(),
                    walletResult.getReason()
            );

            transaction = transactionStateService.changeStatus(transaction, TransactionStatus.FAILED, walletResult.getReason());

            log.info(
                    "Transaction marked as FAILED after wallet validation | transactionId={} | referenceNumber={}",
                    transaction.getId(),
                    transaction.getReferenceNumber()
            );

            return mapToResponse(transaction);
        }


        log.info(
                "Wallet validation successful | transactionId={} | referenceNumber={}",
                transaction.getId(),
                transaction.getReferenceNumber()
        );

        // Wallet validation successful, change status to PROCESSING
        transactionStateService.changeStatus(transaction, TransactionStatus.PROCESSING, "Wallet validation successful, processing transaction");

        log.info(
                "Transaction status changed to PROCESSING | transactionId={} | referenceNumber={}",
                transaction.getId(),
                transaction.getReferenceNumber()
        );

        ResolveAccountsResponse accounts = resolveAccounts(transaction);

        log.info(
                "Ledger accounts resolved successfully | transactionId={} | referenceNumber={}",
                transaction.getId(),
                transaction.getReferenceNumber()
        );



        // 8. Process transaction with ledger service
        log.info(
                "Calling ledger-service | transactionId={} | referenceNumber={} | amount={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                transaction.getAmount()
        );

        JournalResponse journalResponse = executeLedgerTransfer(transaction, accounts);

        // If journalResponse is null, it means the ledger service may have processed the transaction but the response was lost. In this case, we keep the transaction status as PROCESSING and return the response.
        if (journalResponse == null) {

            log.warn(
                    "Ledger response is null | transactionId={} | referenceNumber={} | keeping status=PROCESSING",
                    transaction.getId(),
                    transaction.getReferenceNumber()
            );

            return mapToResponse(transaction);
        }


        log.info(
                "Ledger response received | transactionId={} | referenceNumber={} | journalStatus={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                journalResponse.getStatus()
        );

        // If the journal response indicates a successful completion, we change the transaction status to SUCCESS and return the response.
        if (journalResponse.getStatus() == JournalStatus.COMPLETED) {

            log.info(
                    "Ledger transfer completed successfully | transactionId={} | referenceNumber={}",
                    transaction.getId(),
                    transaction.getReferenceNumber()
            );

            transaction = transactionStateService.changeStatus(transaction, TransactionStatus.SUCCESS, "Ledger transfer completed");

            log.info(
                    "Transaction completed successfully | transactionId={} | referenceNumber={} | status={}",
                    transaction.getId(),
                    transaction.getReferenceNumber(),
                    transaction.getStatus()
            );

            return mapToResponse(transaction);
        }

        // Ledger failed . Change transaction status to FAILED and return response.
        log.error(
                "Ledger transfer failed | transactionId={} | referenceNumber={} | journalStatus={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                journalResponse.getStatus()
        );
        transaction = transactionStateService.changeStatus(transaction, TransactionStatus.FAILED, journalResponse.getStatus().name());

        log.info(
                "Transaction marked as FAILED | transactionId={} | referenceNumber={} | status={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                transaction.getStatus()
        );

        // 9. Return response
        log.info(
                "Transfer completed | transactionId={} | referenceNumber={} | finalStatus={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                transaction.getStatus()
        );

        // Todo : Publidh Event

        return mapToResponse(transaction);

    }


    private Transaction checkIdempotency(String idempotencyKey, String requestHash) {

        return idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey)
                .map(key -> {

                    if (!key.getRequestHash().equals(requestHash)) {

                        throw new IdempotencyConflictException("Idempotency key was already used with a different request");
                    }

                    return transactionRepository.findById(key.getTransactionId()).orElseThrow(() ->
                            new TransactionException(
                                    "Transaction associated with idempotency key not found"
                            )
                    );

                })
                .orElse(null);

    }

    private void validateTransfer(TransferTransactionRequest request) {

        if (request.getSenderWalletId().equals(request.getReceiverWalletId())) {

            throw new TransactionException("Sender and receiver wallet IDs cannot be the same");
        }

        if (request.getAmount().signum() <= 0) {

            throw new TransactionException("Amount must be greater than zero");
        }
    }


    private Transaction buildTransaction(TransferTransactionRequest request) {

        return Transaction.builder()
                .referenceNumber(transactionReferenceGenerator.generate())
                .transactionType(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .currencyCode(request.getCurrencyCode())
                .status(TransactionStatus.PENDING)
                .senderWalletId(request.getSenderWalletId())
                .receiverWalletId(request.getReceiverWalletId())
                .build();

    }


    private WalletTransferValidationResponse validateWallets(Transaction transaction) {

        WalletTransferValidationRequest request =
                new WalletTransferValidationRequest(
                        transaction.getSenderWalletId(),
                        transaction.getReceiverWalletId(),
                        transaction.getAmount(),
                        transaction.getCurrencyCode()
                );

        // Call wallet service to validate wallets
        return walletClient.validateTransfer(request);

    }

    private JournalResponse executeLedgerTransfer(Transaction transaction, ResolveAccountsResponse accounts) {


        UUID senderAccountId =
                accounts.getAccounts()
                        .stream()
                        .filter(account -> account.getWalletId().equals(transaction.getSenderWalletId()))
                        .map(AccountReferenceResponse::getAccountId)
                        .findFirst()
                        .orElseThrow(() -> new TransactionException("Sender ledger account not found"));

        UUID receiverAccountId =
                accounts.getAccounts()
                        .stream()
                        .filter(account -> account.getWalletId().equals(transaction.getReceiverWalletId()))
                        .map(AccountReferenceResponse::getAccountId)
                        .findFirst()
                        .orElseThrow(() -> new TransactionException("Receiver ledger account not found"));

        log.info(
                "Executing ledger transfer | transactionId={} | referenceNumber={} | fromAccountId={} | toAccountId={} | amount={} | currency={}",
                transaction.getId(),
                transaction.getReferenceNumber(),
                senderAccountId,
                receiverAccountId,
                transaction.getAmount(),
                transaction.getCurrencyCode()
        );

        TransferJournalRequest ledgerRequest =
                TransferJournalRequest.builder()
                        .transactionId(transaction.getId())
                        .reference(transaction.getReferenceNumber())
                        .fromAccountId(senderAccountId)
                        .toAccountId(receiverAccountId)
                        .amount(transaction.getAmount())
                        .currencyCode(transaction.getCurrencyCode())
                        .description("Wallet transfer")
                        .build();

        try {

            return ledgerClient.transfer(ledgerRequest);

        } catch (Exception ex) {


            log.error(
                    "Ledger transfer failed | transactionId={} | referenceNumber={} | error={}",
                    transaction.getId(),
                    transaction.getReferenceNumber(),
                    ex.getMessage()
            );

            /*
             * Ledger may have processed the transaction
             * but the response may have been lost.
             *
             * Therefore, keep Transaction = PROCESSING.
             */
            return null;
        }
    }


    private ResolveAccountsResponse resolveAccounts(Transaction transaction) {

        ResolveAccountsRequest request =
                new ResolveAccountsRequest(
                        Arrays.asList(
                                transaction.getSenderWalletId(),
                                transaction.getReceiverWalletId()
                        )
                );

        return ledgerClient.resolveAccounts(request);
    }


    private TransactionResponse mapToResponse(Transaction transaction) {

        return new TransactionResponse(transaction.getId(), transaction.getReferenceNumber(), transaction.getTransactionType(), transaction.getAmount(), transaction.getCurrencyCode(), transaction.getStatus(), transaction.getSenderWalletId(), transaction.getReceiverWalletId(), transaction.getCreatedAt(), transaction.getCompletedAt());
    }


}