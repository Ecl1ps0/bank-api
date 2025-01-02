package com.bank_api.bank.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bank_api.bank.models.enums.AccountStatus;
import com.bank_api.bank.models.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Account type can not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @NotNull(message = "Number can not be null")
    @Pattern(regexp = "^4[0-9]{12}(?:[0-9]{3})?$|^5[1-5][0-9]{14}$", message="Ivalid Account Number: number should match Visa or Master Card number patterns")
    @Column(nullable = false)
    private String number;

    @NotNull
    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal balance;

    @NotNull(message = "Open Date can not be null")
    @PastOrPresent(message = "Open date must be a past or present date")
    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @NotNull(message = "Close Date can not be null")
    @Column(name = "close_date", nullable = false)
    private LocalDate closeDate;

    @NotNull(message = "Account status can not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$", message = "Invalid IBAN format")
    @Column(nullable = false, unique = true)
    private String iban;

    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2,11}$", message = "Invalid SWIFT code format")
    @Column(name = "swift_code", nullable = false)
    private String swiftCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false) 
    @JsonBackReference
    private User creator;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonProperty("creator_id")
    public UUID getCreatorId() {
        return creator != null ? creator.getId() : null;
    }
    
}
