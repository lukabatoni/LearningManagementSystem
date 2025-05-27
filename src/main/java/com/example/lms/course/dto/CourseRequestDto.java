package com.example.lms.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CourseRequestDto(
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 20, message = "Title must be less than 1-20 characters")
    String title,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price,

    @NotNull(message = "Coins paid is required")
    @DecimalMin(value = "0.0", message = "Coins paid cannot be negative")
    BigDecimal coinsPaid,

    @Valid
    CourseSettingsRequestDto settings
) {
}