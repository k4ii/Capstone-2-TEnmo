package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    Logger log= LoggerFactory.getLogger(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;
    private UserDao userDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String sendBucks(long userFromId, long userToId, BigDecimal amount) {

        log.debug("debug level");
        if(userFromId == userToId) {
            log.warn("Trying to send money to self.");
            return "Cannot send money to yourself.";
        }

        if(amount.compareTo(accountDao.getBalance(userFromId)) < 0 && amount.compareTo(new BigDecimal(0)) == 1) {
            try {
                String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                        "VALUES (2, 2, ?, ?, ?)";
                jdbcTemplate.update(sql, accountDao.findAccountByUserId(userFromId).getAccount_id(),
                        accountDao.findAccountByUserId(userToId).getAccount_id(), amount);

                accountDao.receiveAmount(userToId, amount);
                return "Transfer successfully complete.";
            } catch (DataAccessException e) {

                log.error("Error occurred when attempting to add transfer to database.");
                e.printStackTrace();
                throw new RuntimeException(e);

            }
        }

        else {
            log.warn("Error: Attempted to send more than balance.");
            return "Insufficient funds.";
        }
    }

    @Override
    public String requestBucks(long userToId, long userFromId, BigDecimal amount) {

        log.debug("debug level");
        try{
            if(userFromId == userToId) {
                return "Cannot request money to yourself.";
            }
            String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (1, 1, ?, ?, ?)";
            jdbcTemplate.update(sql, accountDao.findAccountByUserId(userToId).getAccount_id(),
                    accountDao.findAccountByUserId(userFromId).getAccount_id(), amount);

            return "Request sent.";

        } catch (DataAccessException e) {
            log.error("Insufficient amount in account.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Transfer> getAllTransfer(long userId) {
        log.debug("debug level.");
        List<Transfer> transferList = new ArrayList<>();

        try {
            String sql = "SELECT transfers.transfer_id, transfer_types.transfer_type_desc, " +
                    "transfer_statuses.transfer_status_desc, transfers.account_from, transfers.account_to, transfers.amount " +
                    "FROM transfers JOIN transfer_types ON transfer_types.transfer_type_id = transfers.transfer_type_id " +
                    "JOIN transfer_statuses ON transfer_statuses.transfer_status_id = transfers.transfer_status_id " +
                    "JOIN accounts ON accounts.account_id = transfers.account_from OR accounts.account_id = transfers.account_to " +
                    "JOIN users ON users.user_id = accounts.user_id " +
                    "WHERE users.user_id = ?";

            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);

            while(result.next()) {
                Transfer transfer = rowMap(result);

                transferList.add(transfer);
            }

        } catch (DataAccessException e) {
            log.error("Error accessing Transfer List");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return transferList;
    }

    private Transfer rowMap(SqlRowSet result) {
        Transfer transfer = new Transfer();
        transfer.setTransfer_id(result.getLong("transfers.transfer_id"));
        transfer.setAmount(result.getBigDecimal("transfers.amount"));
        transfer.setAccount_to(result.getLong("transfers.account_to"));
        transfer.setAccount_from(result.getLong("transfers.account_from"));
        transfer.setTransfer_type_desc(result.getString("transfer_types.transfer_type_desc"));
        transfer.setTransfer_status_desc(result.getString("transfer_statuses.transfer_status_desc"));
        return transfer;
    }

    @Override
    public Transfer getTransferById(long transferId) {
        log.debug("debug level");
        Transfer transfer = null;

        try {
            String sql = "SELECT transfers.transfer_id, transfer_types.transfer_type_desc, " +
                    "transfer_statuses.transfer_status_desc, transfers.account_from, transfers.account_to, transfers.amount " +
                    "FROM transfers JOIN transfer_types ON transfer_types.transfer_type_id = transfers.transfer_type_id " +
                    "JOIN transfer_statuses ON transfer_statuses.transfer_status_id = transfers.transfer_status_id " +
                    "JOIN accounts ON accounts.account_id = transfers.account_from OR accounts.account_id = transfers.account_to " +
                    "JOIN users ON users.user_id = accounts.user_id " +
                    "WHERE transfers.transfer_id = ?";

            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);

            if(result.next()) {
                transfer = rowMap(result);
            }
        } catch (DataAccessException e) {
            log.error("Could not find Transfer from ID provided.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return transfer;
    }

    @Override
    public String transferAcceptance(long transferId) {
        log.debug("debug level");
        Transfer transfer = getTransferById(transferId);

        if (transfer.getTransfer_type_id() == 1) {
            if (accountDao.findAccountById(transfer.getAccount_to()).getBalance().compareTo(transfer.getAmount()) == 1) {
                String sql = "UPDATE transfers SET transfer_status_id = ? " +
                        "WHERE transfer_id = ?";

                jdbcTemplate.update(sql, 2, transferId);

                Accounts accounts = accountDao.findAccountById(transfer.getAccount_from());
                BigDecimal newBalance = accounts.getBalance().add(transfer.getAmount());
                accountDao.findAccountById(transfer.getAccount_from()).setBalance(newBalance);

                Accounts account = accountDao.findAccountById(transfer.getAccount_to());
                BigDecimal newBalanc = account.getBalance().subtract(transfer.getAmount());
                accountDao.findAccountById(transfer.getAccount_to()).setBalance(newBalance);
            }
        }
    }

    @Override
    public List<Transfer> getPendingTransfers(long userId) {
        log.debug("debug level");
        List<Transfer> transferList = new ArrayList<>();

        for (Transfer transfer : getAllTransfer(userId)) {
            if (transfer.getTransfer_status_id() == 1) {
                transferList.add(transfer);
            }
        }
        return transferList;
    }

    @Override
    public String transferRejection(long transferId) {
        log.debug("debug level");

        try {
            String sql = "UPDATE transfers SET transfer_status_id = ? " +
                    "WHERE transfer_id = ?";

            jdbcTemplate.update(sql, 3, transferId);
        } catch (DataAccessException e) {
            log.error("No existing transfer request.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return "Request Successfully Rejected.";
    }


}
