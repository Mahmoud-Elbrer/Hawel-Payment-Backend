package com.hawel.ledger_service.service;

import com.hawel.common_service.event.WalletCreatedEvent;
import com.hawel.ledger_service.entity.Account;

public interface AccountService {
    Account createWalletAccount(WalletCreatedEvent event);
}
