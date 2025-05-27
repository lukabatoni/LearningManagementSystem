package com.example.lms.student.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record StudentResponseDto(
    UUID id,
    String firstName,
    String lastName,
    String email,
    LocalDate dateOfBirth,
    BigDecimal coins,
    Set<UUID> courseIds
) {
}