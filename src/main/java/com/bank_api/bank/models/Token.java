package com.bank_api.bank.models;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Token {
    @Id
    private String id;

    private String ownerId;

    private String token;
}
