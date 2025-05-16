package com.example.lms.student.service;

import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.mapper.StudentMapper;
import com.example.lms.student.model.Student;
import com.example.lms.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {
  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;

  @Transactional
  public StudentResponseDto createStudent(StudentRequestDto requestDto) {
    Student student = studentMapper.toEntity(requestDto);
    Student saved = studentRepository.save(student);
    return studentMapper.toResponseDto(saved);
  }
}
