package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Accounts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.logging.Level;

public class JdbcAccountDao implements AccountDao{
    private final static String USER_ACCOUNT="select * from accounts where user_id=?";
    private final static String ACCOUNT_UPDATE= "UPDATE accounts SET balance=? WHERE account_id =?";
    private final JdbcTemplate jdbcTemplate;
    Logger log = LoggerFactory.getLogger(this.getClass());

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalance(long userId) {
       return findAccountById(userId).getBalance();
    }

    @Override
    public BigDecimal receiveAmount(long userId, BigDecimal amount) {
        Accounts accounts = findAccountByUserId(userId);
        BigDecimal newBalance = accounts.getBalance().add(amount);
        return updateAccount(newBalance,accounts.getAccount_id());
    }

    @Override
    public BigDecimal sendAmount(long userId, BigDecimal amount) {
       Accounts accounts = findAccountById(userId);
       BigDecimal newBalance = accounts.getBalance().subtract(amount);
       return updateAccount(newBalance,accounts.getAccount_id());
    }

    @Override
    public Accounts findAccountByUserId(long userId) {
        log.debug("start finding an account bellowing to a specific user");
        Accounts accounts= null;
        try{
            SqlRowSet result= jdbcTemplate.queryForRowSet(USER_ACCOUNT,userId);
            while(result.next()){
                accounts= rowMap(result);
            }
        }catch (DataAccessException e){
            log.error("error happened when performing the query to find a account by a user id",e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return accounts;
    }

    @Override
    public Accounts findAccountById(long accountId) {
        log.debug("Level debug");
        Accounts accounts= null;
        try{
            String sql="SELECT * FROM accounts WHERE account_id =?";
            SqlRowSet result= jdbcTemplate.queryForRowSet(sql,accountId);
            if(result.next()){
                accounts = rowMap(result);
            }

        }catch (DataAccessException e){
            log.error("no matching account with this id", e);
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
        return accounts;
    }

    private Accounts rowMap(SqlRowSet result){
        Accounts account = new Accounts();
        account.setAccount_id(result.getLong("account_id"));
        account.setBalance(result.getBigDecimal("balance"));
        account.setUser_id(result.getLong("user_id"));
        return account;
    }

    private BigDecimal updateAccount(BigDecimal balance, long id){
        log.debug("debug level");
        try{
            jdbcTemplate.update(ACCOUNT_UPDATE,balance,id);
        }catch (DataAccessException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return balance;
    }

}
