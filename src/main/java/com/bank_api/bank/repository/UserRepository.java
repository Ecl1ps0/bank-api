package com.bank_api.bank.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bank_api.bank.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber")
    public Optional<User> findByPhoneNumber(String phoneNumber);

}
