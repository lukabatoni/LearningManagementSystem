package com.example.lms.course.controller;

import com.example.lms.course.dto.CourseRequestDto;
import com.example.lms.course.dto.CourseResponseDto;
import com.example.lms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "Endpoints for managing courses")
public class CourseController {

  private final CourseService courseService;

  @Operation(
      summary = "Create a new course",
      description = "Creates a new course with the provided details"
  )
  @PostMapping
  public ResponseEntity<CourseResponseDto> createCourse(@RequestBody @Valid CourseRequestDto requestDto) {
    CourseResponseDto response = courseService.createCourse(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Get all courses",
      description = "Retrieves a list of all courses"
  )
  @GetMapping
  public ResponseEntity<Page<CourseResponseDto>> getAllCourses(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "2") int pageSize,
      @RequestParam(defaultValue = "created") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {

    Page<CourseResponseDto> courses = courseService.getAllCourses(page, pageSize, sortBy, direction);
    return ResponseEntity.ok(courses);
  }

  @Operation(
      summary = "Update a course",
      description = "Updates the details of an existing course"
  )
  @PutMapping("/{id}")
  public ResponseEntity<CourseResponseDto> updateCourse(@PathVariable UUID id,
                                                        @RequestBody @Valid CourseRequestDto requestDto) {
    CourseResponseDto response = courseService.updateCourse(id, requestDto);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Delete a course",
      description = "Deletes a course by ID"
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }
}
