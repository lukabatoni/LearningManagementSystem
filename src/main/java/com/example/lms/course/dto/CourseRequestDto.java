package com.example.lms.course.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CourseRequestDto(
    String title,
    String description,
    BigDecimal price,
    BigDecimal coinsPaid,
    CourseSettingsRequestDto settings,
    Set<UUID> studentIds
) {
}