package com.example.lms.course.mapper;

import com.example.lms.course.dto.CourseRequestDto;
import com.example.lms.course.dto.CourseResponseDto;
import com.example.lms.course.dto.CourseSettingsResponseDto;
import com.example.lms.course.model.Course;
import com.example.lms.course.model.CourseSettings;
import com.example.lms.lesson.model.Lesson;
import com.example.lms.student.model.Student;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CourseMapper {
  Course toEntity(CourseRequestDto dto);

  @Mapping(target = "settings", source = "settings", qualifiedByName = "toSettingsResponseDto")
  @Mapping(target = "lessonIds", source = "lessons", qualifiedByName = "mapLessonIds")
  @Mapping(target = "studentIds", source = "students", qualifiedByName = "mapStudentIds")
  CourseResponseDto toResponseDto(Course course);

  @Named("toSettingsResponseDto")
  default CourseSettingsResponseDto toSettingsResponseDto(CourseSettings settings) {
    if (settings == null) return null;
    return new CourseSettingsResponseDto(
        settings.getId(),
        settings.getStartDate(),
        settings.getEndDate(),
        settings.getIsPublic()
    );
  }

  @Named("mapLessonIds")
  default Set<UUID> mapLessonIds(java.util.List<Lesson> lessons) {
    if (lessons == null) return null;
    return lessons.stream()
        .map(Lesson::getId)
        .collect(Collectors.toSet());
  }

  @Named("mapStudentIds")
  default Set<UUID> mapStudentIds(Set<Student> students) {
    if (students == null) return null;
    return students.stream()
        .map(Student::getId)
        .collect(Collectors.toSet());
  }
}
