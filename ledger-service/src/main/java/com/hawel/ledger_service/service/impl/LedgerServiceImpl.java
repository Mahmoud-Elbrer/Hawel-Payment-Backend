package com.hawel.ledger_service.service.impl;

import com.hawel.ledger_service.domain.LedgerPosting;
import com.hawel.ledger_service.dto.request.DepositJournalRequest;
import com.hawel.ledger_service.dto.request.TransferJournalRequest;
import com.hawel.ledger_service.dto.request.WithdrawalJournalRequest;
import com.hawel.ledger_service.dto.response.JournalResponse;
import com.hawel.ledger_service.entity.JournalEntry;
import com.hawel.ledger_service.enums.JournalType;
import com.hawel.ledger_service.factory.LedgerPostingFactory;
import com.hawel.ledger_service.mapper.JournalMapper;
import com.hawel.ledger_service.service.LedgerEngine;
import com.hawel.ledger_service.service.LedgerService;

import com.hawel.ledger_service.service.SystemAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEngine ledgerEngine;

    private final JournalMapper journalMapper;

    private final SystemAccountService systemAccountService;


    @Override
    @Transactional
    public JournalResponse transfer(TransferJournalRequest request) {

        List<LedgerPosting> postings = Arrays.asList(

                LedgerPostingFactory.debit(
                        request.getFromAccountId(),
                        request.getAmount(),
                        request.getCurrency()
                ),

                LedgerPostingFactory.credit(
                        request.getToAccountId(),
                        request.getAmount(),
                        request.getCurrency()
                )
        );

        JournalEntry journal = ledgerEngine.postJournal(
                JournalType.TRANSFER,
                request.getTransactionId(),
                request.getReference(),
                request.getDescription(),
                postings
        );

        return journalMapper.toResponse(journal);
    }

    @Override
    @Transactional
    public JournalResponse deposit(DepositJournalRequest request) {


        log.info(
                "Starting deposit: transactionId={}, reference={}, accountId={}, amount={}, currency={}",
                request.getTransactionId(),
                request.getReference(),
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        );

        UUID cashAccount = systemAccountService.getCashAccountId(request.getCurrency());

        log.debug("Resolved system cash account: currency={}, cashAccountId={}", request.getCurrency(), cashAccount);

        List<LedgerPosting> postings = Arrays.asList(

                LedgerPostingFactory.debit(
                        cashAccount,
                        request.getAmount(),
                        request.getCurrency()
                ),

                LedgerPostingFactory.credit(
                        request.getAccountId(),
                        request.getAmount(),
                        request.getCurrency()
                )
        );

        log.debug("Created deposit postings: debitAccount={}, creditAccount={}, amount={}, currency={}", cashAccount, request.getAccountId(), request.getAmount(), request.getCurrency());


        JournalEntry journal = ledgerEngine.postJournal(
                JournalType.DEPOSIT,
                request.getTransactionId(),
                request.getReference(),
                request.getDescription(),
                postings
        );

        log.info(
                "Deposit journal posted successfully: journalId={}, transactionId={}, reference={}, accountId={}, amount={}, currency={}",
                journal.getId(),
                request.getTransactionId(),
                request.getReference(),
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        );

        return journalMapper.toResponse(journal);
    }

    @Override
    @Transactional
    public JournalResponse withdraw(WithdrawalJournalRequest request) {

        List<LedgerPosting> postings = Arrays.asList(

                LedgerPostingFactory.debit(
                        request.getAccountId(),
                        request.getAmount(),
                        request.getCurrency()
                ),

                LedgerPostingFactory.credit(
                        systemAccountService.getCashAccountId(request.getCurrency()),
                        request.getAmount(),
                        request.getCurrency()
                )
        );

        JournalEntry journal = ledgerEngine.postJournal(
                JournalType.WITHDRAWAL,
                request.getTransactionId(),
                request.getReference(),
                request.getDescription(),
                postings
        );

        return journalMapper.toResponse(journal);
    }


    @Override
    public JournalResponse reverse(UUID journalId) {
        return null;
    }
}