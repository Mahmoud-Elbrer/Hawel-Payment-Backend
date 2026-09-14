# Hawel User, Wallet and Ledger Account Model

## Overview

In Hawel, it is important not to confuse the following concepts:

* `UserType`
* `WalletType`
* `OwnerType`

Each one represents a different responsibility in the system.

---

# 1. UserType

`UserType` defines **who the user is** inside the Hawel platform.

This belongs to the Identity Service.

```java
public enum UserType {

    CUSTOMER,

    TAJER,

    STAFF
}
```

### CUSTOMER

A normal Hawel user.

Example:

```text
Mahmoud
UserType = CUSTOMER
```

The customer can use Hawel to:

* Send money
* Receive money
* Pay merchants
* Scan QR codes
* Use NFC payments

---

### TAJER

A business owner or merchant using the Hawel Tajer application.

Example:

```text
ABC Store Owner
UserType = TAJER
```

The Tajer can:

* Receive customer payments
* Generate QR payment codes
* Accept NFC payments
* View business transactions
* Manage their business wallet

---

### STAFF

Internal Hawel employees.

Example:

```text
Hawel Support Agent
UserType = STAFF
```

Staff users may access internal systems depending on their assigned permissions.

---

# 2. WalletType

`WalletType` defines **the purpose of the wallet**.

It does not define who the user is.

A recommended implementation is:

```java
public enum WalletType {

    PERSONAL,

    BUSINESS,

    SYSTEM
}
```

---

## PERSONAL Wallet

A wallet used by a normal customer.

Example:

```text
User
Type = CUSTOMER
       │
       ▼
Wallet
Type = PERSONAL
```

Example use cases:

* Sending money
* Receiving money
* Paying merchants
* QR payments
* NFC payments

---

## BUSINESS Wallet

A wallet used for business activities.

Example:

```text
User
Type = TAJER
       │
       ▼
Wallet
Type = BUSINESS
```

Example:

```text
ABC Store
       │
       ▼
Business Wallet
```

The wallet receives payments from customers.

---

## SYSTEM Wallet

A wallet owned by the Hawel platform.

Example:

```text
Hawel System
      │
      ▼
SYSTEM Wallet
```

System wallets can be used for:

* Transfer fees
* Merchant commissions
* Settlements
* Operational balances
* Platform revenue

---

# 3. OwnerType

`OwnerType` is used inside the Ledger Service.

It defines who owns the accounting account.

Example:

```java
public enum OwnerType {

    WALLET,

    SYSTEM
}
```

The Ledger Service does not need to know whether a wallet belongs to a CUSTOMER or a TAJER.

The Ledger Account belongs directly to the Wallet.

---

# Complete Architecture Example

## Tajer Flow

```text
User
─────────────────────
id = U001
phone = +249xxxx
userType = TAJER

        │
        │ ownerId
        ▼

Wallet
─────────────────────
id = W001
ownerId = U001
walletType = BUSINESS
currency = SDG

        │
        │ WalletCreatedEvent
        ▼

Kafka

        │
        ▼

Ledger Account
─────────────────────
id = A001
ownerId = W001
ownerType = WALLET
currency = SDG
```

---

# Customer Flow

```text
User
─────────────────────
id = U002
userType = CUSTOMER

        │
        ▼

Wallet
─────────────────────
id = W002
ownerId = U002
walletType = PERSONAL

        │
        ▼

Ledger Account
─────────────────────
id = A002
ownerId = W002
ownerType = WALLET
```

---

# Important Concept

The relationship is:

```text
USER
 │
 │ owns
 ▼
WALLET
 │
 │ represented by
 ▼
LEDGER ACCOUNT
```

Or more generally:

```text
User / Tajer
        │
        ▼
      Wallet
        │
        ▼
   Ledger Account
```

---

# Why UserType and WalletType Are Different

A user may own multiple wallets.

For example, a Tajer may have:

```text
User
userType = TAJER

       │
       ├── Wallet 1
       │   walletType = BUSINESS
       │   currency = SDG
       │
       └── Wallet 2
           walletType = BUSINESS
           currency = USD
```

A customer may also have multiple wallets in the future:

```text
User
userType = CUSTOMER

       │
       ├── Wallet 1
       │   walletType = PERSONAL
       │
       └── Wallet 2
           walletType = SAVINGS
```

Therefore:

| Concept      | Responsibility                     |
| ------------ | ---------------------------------- |
| `UserType`   | Who is the user?                   |
| `WalletType` | What is the purpose of the wallet? |
| `OwnerType`  | Who owns the Ledger Account?       |

---

# Recommended Hawel Design

```text
Identity Service
─────────────────────────
User
├── CUSTOMER
├── TAJER
└── STAFF


Wallet Service
─────────────────────────
Wallet
├── PERSONAL
├── BUSINESS
└── SYSTEM


Ledger Service
─────────────────────────
Account
├── ownerId = walletId
└── ownerType = WALLET
```

---

# Final Rule

Do not use `UserType` to determine the Ledger Account type.

The Ledger Account should be linked to the Wallet:

```text
User
  ↓
Wallet
  ↓
Ledger Account
```

This separation keeps the Hawel architecture flexible and allows users to own multiple wallets with different purposes in the future.
