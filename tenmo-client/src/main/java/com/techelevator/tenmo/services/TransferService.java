package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import io.cucumber.java.sl.Toda;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Scanner;

public class TransferService {

    private final String BASE_URL;
    private AuthenticatedUser authenticatedUser;
    private RestTemplate restTemplate = new RestTemplate();
    Scanner scanner = new Scanner(System.in);

    public TransferService(String BASE_URL, AuthenticatedUser authenticatedUser) {
        this.BASE_URL = BASE_URL;
        this.authenticatedUser = authenticatedUser;
    }

    public Transfer[] allTransfers() {
        Transfer[] transfers = null;

        try {
            transfers = restTemplate.exchange(BASE_URL + "/account/transfer/" + authenticatedUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();

            System.out.println("-----------------------------------------------");
            System.out.println("Transfers");
            System.out.println("ID            From/To                   Amount");
            System.out.println("-----------------------------------------------");
            String userFromOrTo = "";

            for(Transfer transfer : transfers) {
                if(authenticatedUser.getUser().getUsername().equals(transfer.getUserFrom())) {
                    userFromOrTo = "From: " + transfer.getUserFrom();
                } else {
                    userFromOrTo = "To: " + transfer.getUserTo();
                }
                System.out.println(transfer.getTransfer_id() + "\t \t" + userFromOrTo + "\t \t \t" + "$" + transfer.getAmount());
            }

            System.out.println("------------");
            System.out.println("Please enter transfer ID to view details (0 to cancel): ");
            String input = scanner.nextLine();

            if (Integer.parseInt(input) != 0) {
                boolean isTransferIdExist = false;

                for(Transfer transfer : transfers) {
                    if (Integer.parseInt(input) == transfer.getTransfer_id()) {
                        isTransferIdExist = true;

                        Transfer transfer1 = restTemplate.exchange(BASE_URL + "/transfer/" + transfer.getTransfer_id(), HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();
                        System.out.println("--------------------------------------------");
                        System.out.println("Transfer Details");
                        System.out.println("--------------------------------------------");
                        System.out.println("Id: " + input);
                        System.out.println("From: " + transfer1.getUserFrom());
                        System.out.println("To: " + transfer1.getUserTo());
                        System.out.println("Type: " + transfer1.getTransfer_type_desc());
                        System.out.println("Status: " + transfer1.getTransfer_status_desc());
                        System.out.println("Amount: $" + transfer1.getAmount());

                    }
                    else {
                        System.out.println("No transfer ID found.");
                    }
                }
            }
            //What to do when user chooses 0 to cancel?


        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (RestClientException e) {
            e.getMessage();
            throw new RuntimeException(e);
        }
        return transfers;

    }

    public String sendBucks() {
        User[] userArray = null;
        Transfer transfer = new Transfer();

        try {
            userArray = restTemplate.exchange(BASE_URL + "/listUsers", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
            System.out.println("-------------------------------------------");
            System.out.println("Users");
            System.out.println("ID" + "\t\t" + "Name");
            System.out.println("-------------------------------------------");
            for(User user : userArray) {
                System.out.println(user.getId() + "\t\t" + user.getUsername());
            }
            System.out.println("---------\n");
            System.out.println("Enter ID of user you are sending to (0 to cancel):");
            long inputID = scanner.nextLong();
            if (inputID != authenticatedUser.getUser().getId()) {
                System.out.println("Enter amount: ");
                double amount = scanner.nextDouble();
                BigDecimal amountToSend = new BigDecimal(amount);

                transfer.setAmount(amountToSend);
                transfer.setAccount_from(authenticatedUser.getUser().getId());

                //Set/Get user ID in Transfer JDBC and Service
                //transfer.setAccount_to();
            }
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (RestClientException e) {
            e.getMessage();
            throw new RuntimeException(e);

        }
    }

    private HttpEntity<Transfer> transferHttpEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
