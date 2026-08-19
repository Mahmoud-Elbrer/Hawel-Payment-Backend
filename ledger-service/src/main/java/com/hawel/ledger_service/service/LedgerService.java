package com.hawel.ledger_service.service;

import com.hawel.ledger_service.dto.request.DepositJournalRequest;
import com.hawel.ledger_service.dto.request.TransferJournalRequest;
import com.hawel.ledger_service.dto.request.WithdrawalJournalRequest;
import com.hawel.ledger_service.dto.response.JournalResponse;

import java.util.UUID;

public interface LedgerService {
    JournalResponse transfer(TransferJournalRequest request);

    JournalResponse deposit(DepositJournalRequest request);

    JournalResponse withdraw(WithdrawalJournalRequest request);

//    JournalResponse payment(PaymentJournalRequest request);
//
//    JournalResponse refund(RefundJournalRequest request);

    JournalResponse reverse(UUID journalId);
}
