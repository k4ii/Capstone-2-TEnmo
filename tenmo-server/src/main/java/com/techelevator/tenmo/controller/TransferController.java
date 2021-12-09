package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    private TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/account/transfer/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllTransfersByUserID (@PathVariable long userId) {
        return transferDao.getAllTransfer(userId);
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransferByID (@PathVariable long transferId) {
        return transferDao.getTransferById(transferId);
    }

    @RequestMapping(path = "/transfer/send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String sendTransfer (@Valid @RequestBody Transfer transfer) {
        String transferSuccess = transferDao.sendBucks(transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
        return transferSuccess;
    }

    @RequestMapping(path = "/transfer/request", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String sendRequest (@Valid @RequestBody Transfer transfer) {
        String requestSuccess = transferDao.requestBucks(transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
        return requestSuccess;
    }

    @RequestMapping(path = "/transfer/request/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllPendingTransfers (@PathVariable long userId) {
        return transferDao.getPendingTransfers(userId);
    }

    @RequestMapping(path = "/transfer/request/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptTransfer (@Valid @RequestBody Transfer transfer, @PathVariable int id) {
        if (id == 2) {
            return transferDao.transferAcceptance(transfer.getTransfer_id());
        }
        else if (id == 3) {
            return transferDao.transferRejection(transfer.getTransfer_id());
        }
        else
            return "";
    }
}
