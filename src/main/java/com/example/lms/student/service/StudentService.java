package com.example.lms.student.service;

import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.exception.CoinsNotEnoughException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.mapper.StudentMapper;
import com.example.lms.student.model.Student;
import com.example.lms.student.repository.StudentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {
  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final CourseRepository courseRepository;

  private static final String STUDENT_NOT_FOUND = "Student not found with id: ";
  private static final String COURSE_NOT_FOUND = "Course not found with id: ";
  private static final String COINS_NOT_ENOUGH = "Not enough coins to buy the course";


  @Transactional
  @CacheEvict(value = "students", allEntries = true)
  public StudentResponseDto createStudent(@NonNull final StudentRequestDto requestDto) {
    Student student = studentMapper.toEntity(requestDto);
    Student savedStudent = studentRepository.save(student);
    return studentMapper.toResponseDto(savedStudent);
  }

  @Cacheable(value = "students")
  public List<StudentResponseDto> getAllStudents() {
    List<Student> students = studentRepository.findAll();
    return students.stream()
        .map(studentMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public StudentResponseDto updateStudent(@NonNull final UUID id,
                                          @NonNull final StudentRequestDto requestDto) {
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
  public void deleteStudent(@NonNull final UUID id) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
    studentRepository.delete(student);
  }

  @Transactional
  public void buyCourseWithCoins(@NonNull final UUID studentId,
                                 @NonNull final UUID courseId) {
    Student student = studentRepository.findByIdForUpdate(studentId)
        .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + studentId));

    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND + courseId));

    BigDecimal price = course.getPrice();
    if (student.getCoins().compareTo(price) < 0) {
      throw new CoinsNotEnoughException(COINS_NOT_ENOUGH);
    }

    student.setCoins(student.getCoins().subtract(price));
    course.setCoinsPaid(course.getCoinsPaid().add(price));
    course.getStudents().add(student);
    student.getCourses().add(course);

    studentRepository.save(student);
    courseRepository.save(course);
  }
}
