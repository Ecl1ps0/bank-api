package com.bank_api.bank.dto;

import java.time.LocalDate;
import java.util.List;

import com.bank_api.bank.models.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String iin;
    private String name;
    private String surname;
    private String patronymic;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private UserRole role;
    private List<String> accounts;
}
