package com.bank_api.bank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTokenDTO {
    @NotBlank
    private String refreshToken;
}
