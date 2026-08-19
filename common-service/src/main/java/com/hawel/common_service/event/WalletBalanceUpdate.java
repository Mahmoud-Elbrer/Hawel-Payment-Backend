package com.hawel.common_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
// This class represents an event that is published when a wallet's balance is updated. It contains the wallet ID, available balance, and blocked balance.
// why we need this class ? because when have more than one posting in a journal like transfer, we need to update the balance for each affected wallet, so we need to send the walletId and the new balance for each affected wallet.
public class WalletBalanceUpdate {

    private UUID walletId;

    private BigDecimal availableBalance;

    private BigDecimal blockedBalance;
}
