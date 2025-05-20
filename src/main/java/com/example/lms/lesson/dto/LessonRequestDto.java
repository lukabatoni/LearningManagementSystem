package com.example.lms.lesson.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record LessonRequestDto(
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 50, message = "Title must be 1-50 characters")
    String title,

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours")
    Integer duration,

    @NotNull(message = "Course ID is required")
    UUID courseId
) {
}
