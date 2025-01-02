package com.bank_api.bank.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank_api.bank.models.Account;
import com.bank_api.bank.models.User;
import com.bank_api.bank.models.enums.AccountStatus;
import com.bank_api.bank.models.enums.AccountType;
import com.bank_api.bank.repository.AccountRepository;
import com.bank_api.bank.repository.UserRepository;
import com.bank_api.bank.utils.CardRequisiteGenerator;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRequisiteGenerator reqGenerator;

    public Account saveAccount(UUID userId, String accountType, String cardNumberType) {
        Account account = new Account();
        account.setType(AccountType.valueOf(accountType));
        account.setBalance(new BigDecimal(0));
        account.setOpenDate(LocalDate.now());
        account.setCloseDate(LocalDate.now().plusYears((long) 3));
        account.setStatus(AccountStatus.OPENED);
        account.setIban(reqGenerator.generateIban());
        account.setSwiftCode(reqGenerator.generateSwift());
        account.setCreatedAt(LocalDateTime.now());

        Optional<User> user = this.userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        account.setCreator(user.get());

        if (cardNumberType.equalsIgnoreCase("visa")) {
            account.setNumber(reqGenerator.generateVisaCardNumber());
        } else if (cardNumberType.equalsIgnoreCase("mastercard")) {
            account.setNumber(reqGenerator.generateMasterCardNumber());
        } else {
            throw new IllegalArgumentException("Invalid card number");
        }

        return this.accountRepository.save(account);
    }
}
