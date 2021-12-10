package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private final String BASE_URL;
    private AuthenticatedUser authenticatedUser;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountService(String BASE_URL, AuthenticatedUser authenticatedUser) {
        this.BASE_URL = BASE_URL;
        this.authenticatedUser = authenticatedUser;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0);
        try {
            balance = restTemplate.exchange(BASE_URL + "/balance/" + authenticatedUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
            System.out.println("Your current account balance is: $" + balance);

        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (RestClientException e) {
            e.getMessage();
            throw new RuntimeException(e);

        }
        return balance;
    }



    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }


}
