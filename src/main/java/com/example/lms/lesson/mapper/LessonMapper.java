package com.example.lms.lesson.mapper;

import com.example.lms.lesson.dto.LessonRequestDto;
import com.example.lms.lesson.dto.LessonResponseDto;
import com.example.lms.lesson.model.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
  @Mapping(target = "course", ignore = true)
  Lesson toEntity(LessonRequestDto dto);

  @Mapping(target = "courseId", source = "course.id")
  LessonResponseDto toResponseDto(Lesson lesson);
}
