package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Accounts;

import java.math.BigDecimal;

public interface AccountDao {
    BigDecimal getBalance(long userId);
    BigDecimal receiveAmount(long userId,BigDecimal amount);
    BigDecimal sendAmount(long userId, BigDecimal amount);
    Accounts findAccountByUserId(long userId);
    Accounts findAccountById(long accountId);

}
