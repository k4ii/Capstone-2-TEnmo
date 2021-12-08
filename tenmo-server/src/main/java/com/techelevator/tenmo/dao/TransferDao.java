package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    String sendBucks(long userFromId, long userToId, BigDecimal amount);
    String requestBucks(long userToId, long userFromId, BigDecimal amount);
    List<Transfer> getAllTransfer(long userId);
    Transfer getTransferById(long transferId);
    String transferAcceptance(long transferId);
    List<Transfer> getPendingTransfers(long userId);
    String transferRejection(long transferId);
}
