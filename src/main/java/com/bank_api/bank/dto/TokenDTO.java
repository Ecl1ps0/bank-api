package com.bank_api.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
