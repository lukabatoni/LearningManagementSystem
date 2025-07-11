package com.example.lms.student.controller;

import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/students")
@Tag(name = "Student Management", description = "Endpoints for managing students")
public class StudentController {
  private final StudentService studentService;

  @Operation(
      summary = "Create a new student",
      description = "Creates a new student with the provided details"
  )
  @PostMapping
  public ResponseEntity<StudentResponseDto> createStudent(@RequestBody @Valid StudentRequestDto requestDto) {
    StudentResponseDto response = studentService.createStudent(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Get all students",
      description = "Retrieves a list of all students"
  )
  @GetMapping
  public ResponseEntity<List<StudentResponseDto>> getAllStudents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "2") int pageSize,
      @RequestParam(defaultValue = "created") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {

    List<StudentResponseDto> students = studentService.getAllStudents(page, pageSize, sortBy, direction);
    return ResponseEntity.ok(students);
  }

  @Operation(
      summary = "Update a student",
      description = "Updates the details of an existing student"
  )
  @PutMapping("/{id}")
  public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable UUID id,
                                                          @RequestBody @Valid StudentRequestDto requestDto) {
    StudentResponseDto response = studentService.updateStudent(id, requestDto);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Delete a student",
      description = "Deletes a student by ID"
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
    studentService.deleteStudent(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Buy a course with coins",
      description = "Allows a student to buy a course using their coins"
  )
  @PostMapping("/{studentId}/buy-course/{courseId}")
  public ResponseEntity<Void> buyCourseWithCoins(
      @PathVariable UUID studentId,
      @PathVariable UUID courseId) {
    studentService.buyCourseWithCoins(studentId, courseId);
    return ResponseEntity.noContent().build();
  }
}
