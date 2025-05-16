package com.example.lms.student.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StudentRequestDto(
    String firstName,
    String lastName,
    String email,
    LocalDate dateOfBirth,
    BigDecimal coins
) {
}