package com.example.lms.unit.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.enums.LocaleCode;
import com.example.lms.exception.CoinsNotEnoughException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.dto.StudentResponseDto;
import com.example.lms.student.mapper.StudentMapper;
import com.example.lms.student.model.Student;
import com.example.lms.student.repository.StudentRepository;
import com.example.lms.student.service.StudentService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

public class StudentServiceTest {
  private StudentRepository studentRepository;
  private StudentMapper studentMapper;
  private CourseRepository courseRepository;
  private StudentService studentService;

  @BeforeEach
  void setUp() {
    studentRepository = mock(StudentRepository.class);
    studentMapper = mock(StudentMapper.class);
    courseRepository = mock(CourseRepository.class);
    studentService = new StudentService(studentRepository, studentMapper, courseRepository);
  }

  @Test
  void createStudent_shouldSaveAndReturnDto() {
    StudentRequestDto requestDto = new StudentRequestDto(
        "John", "Doe", "john.doe@example.com",
        LocalDate.of(2000, 1, 1), BigDecimal.TEN, LocaleCode.EN
    );
    Student student = new Student();
    Student savedStudent = new Student();
    StudentResponseDto responseDto = new StudentResponseDto(
        UUID.randomUUID(), "John", "Doe", "john.doe@example.com",
        LocalDate.of(2000, 1, 1), BigDecimal.TEN, Set.of()
    );

    when(studentMapper.toEntity(requestDto)).thenReturn(student);
    when(studentRepository.save(student)).thenReturn(savedStudent);
    when(studentMapper.toResponseDto(savedStudent)).thenReturn(responseDto);

    StudentResponseDto result = studentService.createStudent(requestDto);

    assertEquals(responseDto, result);
    verify(studentMapper).toEntity(requestDto);
    verify(studentRepository).save(student);
    verify(studentMapper).toResponseDto(savedStudent);
  }

  @Test
  void getAllStudents_shouldReturnListOfMappedDtos() {
    Student student1 = new Student();
    Student student2 = new Student();
    StudentResponseDto dto1 = new StudentResponseDto(UUID.randomUUID(),
        "A", "B", "a@b.com", null, BigDecimal.ONE, Set.of());
    StudentResponseDto dto2 = new StudentResponseDto(UUID.randomUUID(),
        "C", "D", "c@d.com", null, BigDecimal.TEN, Set.of());

    List<Student> students = List.of(student1, student2);
    Pageable pageable = org.springframework.data.domain.PageRequest
        .of(0, 10, org.springframework.data.domain.Sort.by("created").descending());
    org.springframework.data.domain.Page<Student> page = new org.springframework.data.domain.PageImpl<>(students);

    when(studentRepository.findAll(pageable)).thenReturn(page);
    when(studentMapper.toResponseDto(student1)).thenReturn(dto1);
    when(studentMapper.toResponseDto(student2)).thenReturn(dto2);

    List<StudentResponseDto> result = studentService.getAllStudents(0, 10, "created", "desc");

    assertEquals(List.of(dto1, dto2), result);
    verify(studentRepository).findAll(pageable);
    verify(studentMapper).toResponseDto(student1);
    verify(studentMapper).toResponseDto(student2);
  }

  @Test
  void updateStudent_shouldUpdateAndReturnDto() {
    UUID id = UUID.randomUUID();
    StudentRequestDto requestDto = new StudentRequestDto(
        "John", "Doe", "john.doe@example.com",
        LocalDate.of(1999, 5, 5), BigDecimal.valueOf(50), LocaleCode.EN
    );
    Student existingStudent = new Student();
    Student updatedStudent = new Student();
    StudentResponseDto responseDto = new StudentResponseDto(
        id, "John", "Doe", "john.doe@example.com",
        LocalDate.of(1999, 5, 5), BigDecimal.valueOf(50), Set.of()
    );

    when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));
    when(studentRepository.save(existingStudent)).thenReturn(updatedStudent);
    when(studentMapper.toResponseDto(updatedStudent)).thenReturn(responseDto);

    StudentResponseDto result = studentService.updateStudent(id, requestDto);

    assertEquals("John", existingStudent.getFirstName());
    assertEquals("Doe", existingStudent.getLastName());
    assertEquals("john.doe@example.com", existingStudent.getEmail());
    assertEquals(LocalDate.of(1999, 5, 5), existingStudent.getDateOfBirth());
    assertEquals(BigDecimal.valueOf(50), existingStudent.getCoins());
    assertEquals(responseDto, result);
    verify(studentRepository).save(existingStudent);
    verify(studentMapper).toResponseDto(updatedStudent);
  }

  @Test
  void updateStudent_shouldThrowIfNotFound() {
    UUID id = UUID.randomUUID();
    StudentRequestDto requestDto = new StudentRequestDto("A", "B",
        "a@b.com", null, BigDecimal.ONE, LocaleCode.EN);
    when(studentRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> studentService.updateStudent(id, requestDto));
  }

  @Test
  void deleteStudent_shouldDeleteIfExists() {
    UUID id = UUID.randomUUID();
    Student student = new Student();

    when(studentRepository.findById(id)).thenReturn(Optional.of(student));

    studentService.deleteStudent(id);

    verify(studentRepository).findById(id);
    verify(studentRepository).delete(student);
  }

  @Test
  void deleteStudent_shouldThrowIfNotFound() {
    UUID id = UUID.randomUUID();
    when(studentRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> studentService.deleteStudent(id));
  }

  @Test
  void buyCourseWithCoins_shouldDeductCoinsAndSave() {
    UUID studentId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    Student student = new Student();
    student.setCoins(new BigDecimal("100"));
    Course course = new Course();
    course.setPrice(new BigDecimal("50"));
    course.setCoinsPaid(BigDecimal.ZERO);

    student.setCourses(new HashSet<>());
    course.setStudents(new HashSet<>());

    when(studentRepository.findByIdForUpdate(studentId)).thenReturn(Optional.of(student));
    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

    studentService.buyCourseWithCoins(studentId, courseId);

    assertEquals(new BigDecimal("50"), student.getCoins());
    assertEquals(new BigDecimal("50"), course.getCoinsPaid());
    assertTrue(student.getCourses().contains(course));
    assertTrue(course.getStudents().contains(student));
    verify(studentRepository).save(student);
    verify(courseRepository).save(course);
  }

  @Test
  void buyCourseWithCoins_shouldThrowIfStudentNotFound() {
    UUID studentId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> studentService.buyCourseWithCoins(studentId, courseId));
  }

  @Test
  void buyCourseWithCoins_shouldThrowIfCourseNotFound() {
    UUID studentId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    Student student = new Student();
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
    when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> studentService.buyCourseWithCoins(studentId, courseId));
  }

  @Test
  void buyCourseWithCoins_shouldThrowIfNotEnoughCoins() {
    UUID studentId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    Student student = new Student();
    student.setCoins(new BigDecimal("10"));
    Course course = new Course();
    course.setPrice(new BigDecimal("50"));

    when(studentRepository.findByIdForUpdate(studentId)).thenReturn(Optional.of(student));
    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

    assertThrows(CoinsNotEnoughException.class,
        () -> studentService.buyCourseWithCoins(studentId, courseId));
  }
}
