package com.bank_api.bank.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank_api.bank.models.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
}
