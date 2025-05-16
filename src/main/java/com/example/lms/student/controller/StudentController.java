package com.example.lms.student.controller;

import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
public class StudentController {
  private final StudentService studentService;

  @PostMapping
  public StudentResponseDto createStudent(@RequestBody StudentRequestDto requestDto) {
    return studentService.createStudent(requestDto);
  }
}
