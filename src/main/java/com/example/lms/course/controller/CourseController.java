package com.example.lms.course.controller;

import com.example.lms.course.dto.CourseRequestDto;
import com.example.lms.course.dto.CourseResponseDto;
import com.example.lms.course.service.CourseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

  private final CourseService courseService;

  @PostMapping
  public CourseResponseDto createCourse(@RequestBody CourseRequestDto requestDto) {
    return courseService.createCourse(requestDto);
  }

  @GetMapping
  public List<CourseResponseDto> getAllCourses() {
    return courseService.getAllCourses();
  }

  @PutMapping("/{id}")
  public CourseResponseDto updateCourse(@PathVariable UUID id, @RequestBody CourseRequestDto requestDto) {
    return courseService.updateCourse(id, requestDto);
  }

  @DeleteMapping("/{id}")
  public void deleteCourse(@PathVariable UUID id) {
    courseService.deleteCourse(id);
  }
}
