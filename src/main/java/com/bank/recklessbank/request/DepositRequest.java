package com.bank.recklessbank.request;

import java.math.BigDecimal;

public class DepositRequest {

    private BigDecimal amount;

    public DepositRequest() {}

    public DepositRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount()             { return amount; }
    public void setAmount(BigDecimal amount)  { this.amount = amount; }
}
