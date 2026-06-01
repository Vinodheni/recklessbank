package com.bank.recklessbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferRecord {

    private final String fromAccountId;
    private final String toAccountId;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public TransferRecord(String fromAccountId, String toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId   = toAccountId;
        this.amount        = amount;
        this.timestamp     = LocalDateTime.now();
    }

    public String getFromAccountId()    { return fromAccountId; }
    public String getToAccountId()      { return toAccountId; }
    public BigDecimal getAmount()       { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
