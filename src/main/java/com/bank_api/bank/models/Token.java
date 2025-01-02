package com.bank_api.bank.models;

import lombok.Data;

@Data
public class Token {
    private String id;

    private String ownerId;

    private String token;
}
