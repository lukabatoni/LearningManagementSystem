package com.example.lms.course.service;

import com.example.lms.course.dto.CourseRequestDto;
import com.example.lms.course.dto.CourseResponseDto;
import com.example.lms.course.mapper.CourseMapper;
import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {
  private final CourseRepository courseRepository;
  private final CourseMapper courseMapper;

  private static final String COURSE_NOT_FOUND = "Course not found with id: ";

  @Transactional
  public CourseResponseDto createCourse(@NonNull final CourseRequestDto courseRequestDto) {
    Course course = courseMapper.toEntity(courseRequestDto);
    Course savedCourse = courseRepository.save(course);
    return courseMapper.toResponseDto(savedCourse);
  }

  //@Transactional - needed when OIVS is disabled
  @Cacheable(value = "courses")
  public List<CourseResponseDto> getAllCourses() {
    List<Course> courses = courseRepository.findAll();
    return courses.stream()
        .map(courseMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public CourseResponseDto updateCourse(@NonNull final UUID id,
                                        @NonNull final CourseRequestDto courseRequestDto) {
    Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND + id));
    course.setTitle(courseRequestDto.title());
    course.setDescription(courseRequestDto.description());
    course.setPrice(courseRequestDto.price());
    course.setCoinsPaid(courseRequestDto.coinsPaid());

    course.getSettings().setStartDate(courseRequestDto.settings().startDate());
    course.getSettings().setEndDate(courseRequestDto.settings().endDate());
    course.getSettings().setIsPublic(courseRequestDto.settings().isPublic());


    Course uptadetCourse = courseRepository.save(course);
    return courseMapper.toResponseDto(uptadetCourse);
  }

  @Transactional
  public void deleteCourse(@NonNull final UUID id) {
    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND + id));
    courseRepository.delete(course);
  }

}
