package com.bank.recklessbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Account {

    private final String id;
    private final String ownerName;
    private final AtomicReference<BigDecimal> balance;
    private final LocalDateTime createdAt;

    // Circular ring buffer — last 50 outgoing transfers, O(1) insert, fixed memory
    private static final int MAX_TRANSFERS = 50;

    // Could have used Deque<TransferRecord> transfers = new ConcurrentLinkedDeque<>(); but did not use it because Array is strictly bounded/fixed memory and less heavy on memory
    private final TransferRecord[] ringBuffer = new TransferRecord[MAX_TRANSFERS];
    private int head = 0;
    private int size = 0;

    public Account(String ownerName, BigDecimal initialBalance) {
        this.id = UUID.randomUUID().toString();
        this.ownerName = ownerName;
        this.balance = new AtomicReference<>(initialBalance);
        this.createdAt = LocalDateTime.now();
    }

    public void deposit(BigDecimal amount) {
        balance.updateAndGet(b -> b.add(amount));
    }

    public void withdraw(BigDecimal amount) {
        balance.updateAndGet(current -> {
            if (current.compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds");
            }
            return current.subtract(amount);
        });
    }

    public synchronized void recordOutgoingTransfer(TransferRecord transfer) {
        ringBuffer[head] = transfer;
        head = (head + 1) % MAX_TRANSFERS;
        if (size < MAX_TRANSFERS) size++;
    }

    public synchronized List<TransferRecord> getOutgoingTransfers() {
        List<TransferRecord> result = new ArrayList<>(size);
        int start = size < MAX_TRANSFERS ? 0 : head;
        for (int i = 0; i < size; i++) {
            result.add(ringBuffer[(start + i) % MAX_TRANSFERS]);
        }
        return result;
    }

    public String getId()               { return id; }
    public String getOwnerName()        { return ownerName; }
    public BigDecimal getBalance()      { return balance.get(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
