package com.example.lms.course.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CourseResponseDto(
    UUID id,
    String title,
    String description,
    BigDecimal price,
    BigDecimal coinsPaid,
    CourseSettingsResponseDto settings,
    Set<UUID> lessonIds,
    Set<UUID> studentIds
) {
}