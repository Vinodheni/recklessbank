package com.bank.recklessbank.request;

import java.math.BigDecimal;

public class CreateAccountRequest {

    private String ownerName;
    private BigDecimal initialBalance;

    public CreateAccountRequest() {}

    public CreateAccountRequest(String ownerName, BigDecimal initialBalance) {
        this.ownerName      = ownerName;
        this.initialBalance = initialBalance;
    }

    public String getOwnerName()           { return ownerName; }
    public void setOwnerName(String name)  { this.ownerName = name; }

    public BigDecimal getInitialBalance()              { return initialBalance; }
    public void setInitialBalance(BigDecimal balance)  { this.initialBalance = balance; }
}
