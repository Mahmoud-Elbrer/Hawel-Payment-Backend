package com.hawel.ledger_service.entity;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.ledger_service.enums.AccountStatus;
import com.hawel.ledger_service.enums.AccountType;
import com.hawel.ledger_service.enums.OwnerType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_owner",
                        columnNames = {"owner_id", "owner_type"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_account_number",
                        columnList = "account_number"
                ),
                @Index(
                        name = "idx_owner_id",
                        columnList = "owner_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Internal ledger account number
     * Example:
     * ACC000001
     */
    //    // هذا رقم الحساب في Ledger
    //    يستخدم في:
    //    التقارير
    //    التدقيق
    //    كشف الحساب
    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;

    // Who owns this account? is wallet_id
    // Account.ownerId = Wallet.id
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    // who owns this account?
    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 30)
    private OwnerType ownerType;

    // Accounting purpose
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 50)
    private AccountType accountType;


//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 10)
//    private CurrencyCode currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, length = 10)
    private CurrencyCode currencyCode = CurrencyCode.SDG;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "system_code", length = 50)
    private String systemCode;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = AccountStatus.ACTIVE;
        }
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}