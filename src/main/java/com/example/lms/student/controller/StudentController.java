package com.example.lms.student.controller;

import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public StudentResponseDto createStudent(@RequestBody StudentRequestDto requestDto) {
    return studentService.createStudent(requestDto);
  }

  @GetMapping
  public List<StudentResponseDto> getAllStudents() {
    return studentService.getAllStudents();
  }

  @PutMapping
  public StudentResponseDto updateStudent(UUID id, @RequestBody StudentRequestDto requestDto) {
    return studentService.updateStudent(id, requestDto);
  }

  @DeleteMapping
  public void deleteStudent(UUID id) {
    studentService.deleteStudent(id);
  }
}
