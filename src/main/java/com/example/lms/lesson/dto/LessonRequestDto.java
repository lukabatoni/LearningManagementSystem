package com.example.lms.lesson.dto;

import java.util.UUID;

public record LessonRequestDto(
    String title,
    Integer duration,
    UUID courseId
) {
}
