package com.example.lms.course.service;

import com.example.lms.course.dto.CourseRequestDto;
import com.example.lms.course.dto.CourseResponseDto;
import com.example.lms.course.mapper.CourseMapper;
import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {
  private final CourseRepository courseRepository;
  private final CourseMapper courseMapper;

  @Transactional
  public CourseResponseDto createCourse(CourseRequestDto courseRequestDto) {
    Course course = courseMapper.toEntity(courseRequestDto);
    Course savedCourse = courseRepository.save(course);
    return courseMapper.toResponseDto(savedCourse);
  }


  public List<CourseResponseDto> getAllCourses() {
    List<Course> courses = courseRepository.findAll();
    return courses.stream()
        .map(courseMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public CourseResponseDto updateCourse(UUID id, CourseRequestDto courseRequestDto) {
    Course course = courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
    course.setTitle(courseRequestDto.title());
    course.setDescription(courseRequestDto.description());
    course.setPrice(courseRequestDto.price());
    course.setCoinsPaid(courseRequestDto.coinsPaid());

    Course uptadetCourse = courseRepository.save(course);
    return courseMapper.toResponseDto(uptadetCourse);
  }

  @Transactional
  public void deleteCourse(UUID id) {
    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
    courseRepository.delete(course);
  }

}
