package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDao implements TransferDao{
    Logger log= LoggerFactory.getLogger(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String sendBucks(long userFromId, long userToId, BigDecimal amount) {
        if(userFromId == userToId) {
            return "Cannot send money to yourself.";
        }
        if(amount.compareTo(accountDao.getBalance(userFromId)) < 0 && amount.compareTo(new BigDecimal(0)) == 1) {
            String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (2, 2, ?, ?, ?)";
            jdbcTemplate.update(sql, accountDao.findAccountByUserId(userFromId).getAccount_id(), accountDao.findAccountById(userToId).getAccount_id(), amount);

            return "Transfer successfully complete.";
        }
        else
            return "Insufficient funds.";
    }

    @Override
    public String requestBucks(long userToId, long userFromId, BigDecimal amount) {
        if(userFromId == userToId) {
            return "Cannot request money to yourself.";
        }
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)"
    }

    @Override
    public List<Transfer> getAllTransfer(long userId) {
        return null;
    }

    @Override
    public Transfer getTransferById(long transferId) {
        return null;
    }

    @Override
    public String transferAcceptance(long transferId, int transferStatusId) {
        return null;
    }
}
