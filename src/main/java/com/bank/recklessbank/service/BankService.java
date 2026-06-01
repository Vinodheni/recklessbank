package com.bank.recklessbank.service;

import com.bank.recklessbank.model.Account;
import com.bank.recklessbank.model.TransferRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankService {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account createAccount(String ownerName, BigDecimal initialBalance) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name must not be blank");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance must be >= 0");
        }
        Account account = new Account(ownerName, initialBalance);
        accounts.put(account.getId(), account);
        return account;
    }

    public Account deposit(String accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = getOrThrow(accountId);
        account.deposit(amount);
        return account;
    }

    public Account withdraw(String accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = getOrThrow(accountId);
        account.withdraw(amount);
        return account;
    }

    public void transfer(String fromId, String toId, BigDecimal amount) {
        requirePositive(amount);
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account from = getOrThrow(fromId);
        Account to   = getOrThrow(toId);

        // Lock in consistent ID order to prevent deadlocks
        Account first  = fromId.compareTo(toId) < 0 ? from : to;
        Account second = fromId.compareTo(toId) < 0 ? to   : from;

        synchronized (first) {
            synchronized (second) {
                from.withdraw(amount);
                to.deposit(amount);
                from.recordOutgoingTransfer(new TransferRecord(fromId, toId, amount));
            }
        }
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    public Account getAccount(String id) {
        return getOrThrow(id);
    }

    public List<TransferRecord> getOutgoingTransfers(String accountId) {
        return getOrThrow(accountId).getOutgoingTransfers();
    }

    private Account getOrThrow(String id) {
        return Optional.ofNullable(accounts.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
