package com.hawel.ledger_service.service;

import com.hawel.common_service.dto.ledger.JournalResponse;
import com.hawel.common_service.dto.ledger.ResolveAccountsRequest;
import com.hawel.common_service.dto.ledger.ResolveAccountsResponse;
import com.hawel.common_service.dto.ledger.TransferJournalRequest;
import com.hawel.ledger_service.dto.request.*;

import java.util.UUID;

public interface LedgerService {
    JournalResponse transfer(TransferJournalRequest request);

    JournalResponse deposit(DepositJournalRequest request);

    JournalResponse withdraw(WithdrawalJournalRequest request);

    // this later  : payment ,  refund, reverse after merchant system
    JournalResponse payment(PaymentJournalRequest request);

    JournalResponse refund(RefundJournalRequest request);

    JournalResponse reverse(UUID journalId);


    //This Internal method is used to find all accounts to Transaction Service to check if the owner has any accounts before creating a transaction
    ResolveAccountsResponse resolveAccounts(ResolveAccountsRequest request);
}
