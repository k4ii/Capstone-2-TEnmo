package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Component
public class JdbcAccountDao implements AccountDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao() {
    }

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalance(int userId) {
        String sqlString = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = null;
        BigDecimal balance = null;
        try {
            results = jdbcTemplate.queryForRowSet(sqlString, userId);
            if (results.next()) {
                balance = results.getBigDecimal("balance");
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return balance;
    }

    @Override
    public BigDecimal addToBalance(BigDecimal amountToAdd, int id) {
        Accounts account = findAccountById(id);
        BigDecimal newBalance = account.getBalance().add(amountToAdd);
        System.out.println(newBalance);
        String sqlString = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try {
            jdbcTemplate.update(sqlString, newBalance, id);
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return account.getBalance();
    }

    @Override
    public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id) {
        Accounts account = findAccountById(id);
        BigDecimal newBalance = account.getBalance().subtract(amountToSubtract);
        String sqlString = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try {
            jdbcTemplate.update(sqlString, newBalance, id);
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return account.getBalance();
    }

    @Override
    public Accounts findUserById(int userId) {

        Accounts account = null;
        try {
            String sqlString = "SELECT * FROM accounts WHERE user_id = ?";
            SqlRowSet result = jdbcTemplate.queryForRowSet(sqlString, userId);
           if(result.next()){
               account = mapRowToAccount(result);
           }

        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return account;
    }

    @Override
    public Accounts findAccountById(int id) {
        Accounts account = null;
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    private Accounts mapRowToAccount(SqlRowSet result) {
        Accounts account = new Accounts();
        account.setBalance(result.getBigDecimal("balance"));
        account.setAccount_id(result.getInt("account_id"));
        account.setUser_id(result.getInt("user_id"));
        return account;
    }
}
