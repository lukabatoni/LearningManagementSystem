package com.example.lms.course.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseSettingsResponseDto(
    UUID id,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isPublic
) {
}
