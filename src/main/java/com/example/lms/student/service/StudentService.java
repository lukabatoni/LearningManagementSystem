package com.example.lms.student.service;

import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.mapper.StudentMapper;
import com.example.lms.student.model.Student;
import com.example.lms.student.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {
  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;

  private static final String STUDENT_NOT_FOUND = "Student not found with id: ";


  @Transactional
  public StudentResponseDto createStudent(StudentRequestDto requestDto) {
    Student student = studentMapper.toEntity(requestDto);
    Student savedStudent = studentRepository.save(student);
    return studentMapper.toResponseDto(savedStudent);
  }

  public List<StudentResponseDto> getAllStudents() {
    List<Student> students = studentRepository.findAll();
    return students.stream()
        .map(studentMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public StudentResponseDto updateStudent(UUID id, StudentRequestDto requestDto) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));

    student.setFirstName(requestDto.firstName());
    student.setLastName(requestDto.lastName());
    student.setEmail(requestDto.email());
    student.setDateOfBirth(requestDto.dateOfBirth());
    student.setCoins(requestDto.coins());
    Student updatedStudent = studentRepository.save(student);
    return studentMapper.toResponseDto(updatedStudent);
  }

  @Transactional
  public void deleteStudent(UUID id) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
    studentRepository.delete(student);
  }
}
