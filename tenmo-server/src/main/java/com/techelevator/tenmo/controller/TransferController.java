package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
    @Autowired
    private TransferDao transfersDAO;

    @RequestMapping(value = "account/transfers/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllMyTransfers(@PathVariable int id) {
        List<Transfer> output = transfersDAO.getAllTransfers(id);
        return output;
    }

    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public Transfer getSelectedTransfer(@PathVariable int id) throws TransferNotFoundException {
        Transfer transfer =transfersDAO.getTransferById(id);
        return transfer;
    }

    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public String sendTransferRequest(@RequestBody Transfer transfer) {
        String results = transfersDAO.sendTransfer(transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
        return results;
    }

    @RequestMapping(path = "request", method = RequestMethod.POST)
    public String requestTransferRequest(@RequestBody Transfer transfer) {
        String results = transfersDAO.requestTransfer(transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
        return results;
    }

    @RequestMapping(value = "request/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllTransferRequests(@PathVariable int id) {
        List<Transfer> output = transfersDAO.getPendingRequests(id);
        return output;
    }

    @RequestMapping(path = "transfer/status/{statusId}", method = RequestMethod.PUT)
    public String updateRequest(@RequestBody Transfer transfer, @PathVariable int statusId) {
        String output = transfersDAO.updateTransferRequest(transfer, statusId);
        return output;
    }

}
