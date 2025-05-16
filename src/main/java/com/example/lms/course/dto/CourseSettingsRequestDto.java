package com.example.lms.course.dto;

import java.time.LocalDateTime;

public record CourseSettingsRequestDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isPublic
) {
}