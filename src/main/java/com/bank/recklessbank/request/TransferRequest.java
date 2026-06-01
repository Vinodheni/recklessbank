package com.bank.recklessbank.request;

import java.math.BigDecimal;

public class TransferRequest {

    private String toAccountId;
    private BigDecimal amount;

    public TransferRequest() {}

    public TransferRequest(String toAccountId, BigDecimal amount) {
        this.toAccountId = toAccountId;
        this.amount      = amount;
    }

    public String getToAccountId()              { return toAccountId; }
    public void setToAccountId(String toId)     { this.toAccountId = toId; }

    public BigDecimal getAmount()               { return amount; }
    public void setAmount(BigDecimal amount)    { this.amount = amount; }
}
