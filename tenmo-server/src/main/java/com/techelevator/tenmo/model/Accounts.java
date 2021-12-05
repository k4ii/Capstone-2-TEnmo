package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Accounts {
    private long user_id;
    private long account_id;
    private BigDecimal balance;

    public Accounts(long user_id, long account_id, BigDecimal balance) {
        this.user_id = user_id;
        this.account_id = account_id;
        this.balance = balance;
    }

    public Accounts() {
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Accounts{" +
                "user_id=" + user_id +
                ", account_id=" + account_id +
                ", balance=" + balance +
                '}';
    }
}
