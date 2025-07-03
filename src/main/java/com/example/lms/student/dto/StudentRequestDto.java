package com.example.lms.student.dto;

import com.example.lms.enums.LocaleCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record StudentRequestDto(
    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z\\-\\s']+$", message = "First name contains invalid characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z\\-\\s']+$", message = "Last name contains invalid characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    @NotNull(message = "Coins balance is required")
    @DecimalMin(value = "0.0", message = "Coins cannot be negative")
    BigDecimal coins,

    @NotNull(message = "Locale is required")
    LocaleCode locale

) {
}