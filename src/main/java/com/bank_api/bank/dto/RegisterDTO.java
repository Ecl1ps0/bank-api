package com.bank_api.bank.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    @Pattern(regexp="\\d{12}", message="Invalid IIN: IIN should has 12 digits")
    private String iin;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 25, message = "Name must be between 2 and 25 characters")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 25, message = "Surname must be between 2 and 25 characters")
    private String surname;

    @Size(max = 25, message = "Patronymic must not exceed 25 characters")
    private String patronymic;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = 100, message = "Address must not exceed 100 characters")
    private String address;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Password can not be null")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
        message = "Password must be 8-20 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;
    
}
