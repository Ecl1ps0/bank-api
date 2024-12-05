package com.bank_api.bank.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bank_api.bank.models.enums.UserRole;
import com.mongodb.lang.Nullable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="users")
public class User implements UserDetails {
    @Id
    private ObjectId id;

    @NotNull(message = "IIN can not be null")
    @Pattern(regexp = "\\d{12}", message="Invalid IIN: IIN should has 12 digits")
    private String iin;

    @NotNull(message = "Name can not be null")
    @Size(min = 2, max = 25, message="Name must be between 2 and 25 characters")
    private String name;

    @NotNull(message = "Surname can not be null")
    @Size(min = 2, max = 25, message="Surname must be between 2 and 25 characters")
    private String surname;

    @Nullable
    private String patronymic;

    @NotNull(message = "Email can not be null")
    @Email(message = "Invalid Email address")
    private String email;

    @Indexed(unique = true)
    @NotNull(message = "Phone number can not be null")
    private String phoneNumber;

    @Nullable
    private String address;

    @NotNull(message = "Birthday can not be null")
    @Past(message = "Date of birth must be a past date")
    private LocalDate dateOfBirth;

    private UserRole role;

    @NotNull(message = "Password can not be null")
    private String password;

    @NotNull(message = "Creation date can not be null")
    private LocalDateTime createdAt;

    @Nullable
    private LocalDateTime updatedAt;

    @Nullable
    private LocalDateTime deletedAt;

    @Override
    public String getUsername() {
        return this.phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "Role_" + this.role);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
}
