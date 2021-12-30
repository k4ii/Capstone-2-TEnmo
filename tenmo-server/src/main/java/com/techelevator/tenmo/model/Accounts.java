package com.techelevator.tenmo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Generated;
import java.math.BigDecimal;

public class Accounts {

    private int user_id;
    private int account_id;
    private BigDecimal balance;


    public Accounts() {
    }

    public Accounts(int user_id, int account_id, BigDecimal balance) {
        this.user_id = user_id;
        this.account_id = account_id;
        this.balance = balance;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
