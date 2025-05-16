package com.example.lms.student.mapper;

import com.example.lms.course.model.Course;
import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.model.Student;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudentMapper {
  Student toEntity(StudentRequestDto dto);

  @Mapping(target = "courseIds", source = "courses", qualifiedByName = "mapCourseIds")
  StudentResponseDto toResponseDto(Student student);

  @Named("mapCourseIds")
  default Set<UUID> mapCourseIds(Set<Course> courses) {
    if (courses == null) return null;
    return courses.stream().map(Course::getId).collect(Collectors.toSet());
  }
}
