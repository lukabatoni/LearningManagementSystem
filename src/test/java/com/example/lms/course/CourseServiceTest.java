package com.example.lms.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.lms.course.dto.CourseRequestDto;
import com.example.lms.course.dto.CourseResponseDto;
import com.example.lms.course.dto.CourseSettingsRequestDto;
import com.example.lms.course.dto.CourseSettingsResponseDto;
import com.example.lms.course.mapper.CourseMapper;
import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.course.service.CourseService;
import com.example.lms.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CourseServiceTest {
  private CourseRepository courseRepository;
  private CourseMapper courseMapper;
  private CourseService courseService;

  @BeforeEach
  void setUp() {
    courseRepository = mock(CourseRepository.class);
    courseMapper = mock(CourseMapper.class);
    courseService = new CourseService(courseRepository, courseMapper);
  }

  @Test
  void createCourse_shouldSaveAndReturnDto() {
    CourseSettingsRequestDto settings = new CourseSettingsRequestDto(
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(10),
        true
    );
    CourseRequestDto requestDto = new CourseRequestDto(
        "Java 101",
        "Intro to Java",
        BigDecimal.valueOf(100),
        BigDecimal.valueOf(10),
        settings
    );
    Course course = new Course();
    Course savedCourse = new Course();
    CourseResponseDto responseDto = new CourseResponseDto(
        UUID.randomUUID(),
        "Java 101",
        "Intro to Java",
        BigDecimal.valueOf(100),
        BigDecimal.valueOf(10),
        new CourseSettingsResponseDto(UUID.randomUUID(),
            settings.startDate(), settings.endDate(), settings.isPublic()),
        Set.of(),
        Set.of()
    );

    when(courseMapper.toEntity(requestDto)).thenReturn(course);
    when(courseRepository.save(course)).thenReturn(savedCourse);
    when(courseMapper.toResponseDto(savedCourse)).thenReturn(responseDto);

    CourseResponseDto result = courseService.createCourse(requestDto);

    assertEquals(responseDto, result);
    verify(courseMapper).toEntity(requestDto);
    verify(courseRepository).save(course);
    verify(courseMapper).toResponseDto(savedCourse);
  }

  @Test
  void getAllCourses_shouldReturnMappedDtos() {
    Course course1 = new Course();
    Course course2 = new Course();
    CourseSettingsResponseDto settings1 = new CourseSettingsResponseDto(UUID.randomUUID(),
        LocalDateTime.now(), LocalDateTime.now().plusDays(1), true);
    CourseSettingsResponseDto settings2 = new CourseSettingsResponseDto(UUID.randomUUID(),
        LocalDateTime.now(), LocalDateTime.now().plusDays(2), false);

    CourseResponseDto dto1 = new CourseResponseDto(
        UUID.randomUUID(), "A", "descA", BigDecimal.ONE, BigDecimal.ZERO, settings1, Set.of(), Set.of()
    );
    CourseResponseDto dto2 = new CourseResponseDto(
        UUID.randomUUID(), "B", "descB", BigDecimal.TEN, BigDecimal.ONE, settings2, Set.of(), Set.of()
    );

    when(courseRepository.findAll()).thenReturn(List.of(course1, course2));
    when(courseMapper.toResponseDto(course1)).thenReturn(dto1);
    when(courseMapper.toResponseDto(course2)).thenReturn(dto2);

    List<CourseResponseDto> result = courseService.getAllCourses();

    assertEquals(List.of(dto1, dto2), result);
    verify(courseRepository).findAll();
    verify(courseMapper).toResponseDto(course1);
    verify(courseMapper).toResponseDto(course2);
  }

  @Test
  void updateCourse_shouldUpdateAndReturnDto() {
    UUID courseId = UUID.randomUUID();
    CourseSettingsRequestDto settings = new CourseSettingsRequestDto(
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(10),
        false
    );
    CourseRequestDto requestDto = new CourseRequestDto(
        "Updated Title",
        "Updated Desc",
        BigDecimal.valueOf(200),
        BigDecimal.valueOf(20),
        settings
    );
    Course existingCourse = new Course();
    existingCourse.setTitle("Old Title");
    existingCourse.setDescription("Old Desc");
    existingCourse.setPrice(BigDecimal.valueOf(100));
    existingCourse.setCoinsPaid(BigDecimal.valueOf(10));
    var existingSettings = new com.example.lms.course.model.CourseSettings();
    existingSettings.setStartDate(LocalDateTime.now());
    existingSettings.setEndDate(LocalDateTime.now().plusDays(5));
    existingSettings.setIsPublic(true);
    existingCourse.setSettings(existingSettings);

    Course updatedCourse = new Course();
    CourseSettingsResponseDto settingsResponse = new CourseSettingsResponseDto(
        UUID.randomUUID(), settings.startDate(), settings.endDate(), settings.isPublic()
    );
    CourseResponseDto responseDto = new CourseResponseDto(
        courseId,
        "Updated Title",
        "Updated Desc",
        BigDecimal.valueOf(200),
        BigDecimal.valueOf(20),
        settingsResponse,
        Set.of(),
        Set.of()
    );

    when(courseRepository.findById(courseId)).thenReturn(java.util.Optional.of(existingCourse));
    when(courseRepository.save(existingCourse)).thenReturn(updatedCourse);
    when(courseMapper.toResponseDto(updatedCourse)).thenReturn(responseDto);

    CourseResponseDto result = courseService.updateCourse(courseId, requestDto);

    assertEquals("Updated Title", existingCourse.getTitle());
    assertEquals("Updated Desc", existingCourse.getDescription());
    assertEquals(BigDecimal.valueOf(200), existingCourse.getPrice());
    assertEquals(BigDecimal.valueOf(20), existingCourse.getCoinsPaid());
    assertEquals(settings.startDate(), existingCourse.getSettings().getStartDate());
    assertEquals(settings.endDate(), existingCourse.getSettings().getEndDate());
    assertEquals(settings.isPublic(), existingCourse.getSettings().getIsPublic());
    assertEquals(responseDto, result);
    verify(courseRepository).save(existingCourse);
    verify(courseMapper).toResponseDto(updatedCourse);
  }

  @Test
  void updateCourse_shouldThrowIfCourseNotFound() {
    UUID courseId = UUID.randomUUID();
    CourseRequestDto requestDto = new CourseRequestDto(
        "Title", "Desc", BigDecimal.valueOf(100), BigDecimal.valueOf(10),
        new CourseSettingsRequestDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), true)
    );

    when(courseRepository.findById(courseId)).thenReturn(java.util.Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(courseId, requestDto));
  }

  @Test
  void deleteCourse_shouldDeleteIfExists() {
    UUID courseId = UUID.randomUUID();
    Course course = new Course();

    when(courseRepository.findById(courseId)).thenReturn(java.util.Optional.of(course));

    courseService.deleteCourse(courseId);

    verify(courseRepository).findById(courseId);
    verify(courseRepository).delete(course);
  }

  @Test
  void deleteCourse_shouldThrowIfNotFound() {
    UUID courseId = UUID.randomUUID();
    when(courseRepository.findById(courseId)).thenReturn(java.util.Optional.empty());
    assertThrows(ResourceNotFoundException.class,
        () -> courseService.deleteCourse(courseId));
  }
}
