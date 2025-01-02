package com.bank_api.bank.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bank_api.bank.models.Account;
import com.bank_api.bank.service.AccountService;


@Controller
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;

    @PostMapping("/{userId}")
    public ResponseEntity<Account> createAccount(@PathVariable UUID userId, @RequestParam("type") String accountType, @RequestParam("card") String cardType) {
        Account account = this.accountService.saveAccount(userId, accountType, cardType);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    

}
