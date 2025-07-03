package com.example.lms.unit.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.lesson.dto.LessonRequestDto;
import com.example.lms.lesson.dto.LessonResponseDto;
import com.example.lms.lesson.mapper.LessonMapper;
import com.example.lms.lesson.model.Lesson;
import com.example.lms.lesson.repository.LessonRepository;
import com.example.lms.lesson.service.LessonService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LessonServiceTest {
  private LessonRepository lessonRepository;
  private LessonMapper lessonMapper;
  private CourseRepository courseRepository;
  private LessonService lessonService;

  @BeforeEach
  void setUp() {
    lessonRepository = mock(LessonRepository.class);
    lessonMapper = mock(LessonMapper.class);
    courseRepository = mock(CourseRepository.class);
    lessonService = new LessonService(lessonRepository, lessonMapper, courseRepository);
  }

  @Test
  void createLesson_shouldSaveAndReturnDto() {
    UUID courseId = UUID.randomUUID();
    LessonRequestDto requestDto = new LessonRequestDto("Title", 60, courseId);
    Course course = new Course();
    Lesson lesson = mock(Lesson.class);
    Lesson savedLesson = mock(Lesson.class);
    LessonResponseDto responseDto =
        new LessonResponseDto(UUID.randomUUID(), "Title", 60, courseId);

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    when(lessonMapper.toEntity(requestDto)).thenReturn(lesson);
    when(lessonRepository.save(lesson)).thenReturn(savedLesson);
    when(lessonMapper.toResponseDto(savedLesson)).thenReturn(responseDto);

    LessonResponseDto result = lessonService.createLesson(requestDto);

    assertEquals(responseDto, result);
    verify(lesson).setCourse(course);
    verify(lessonRepository).save(lesson);
    verify(lessonMapper).toResponseDto(savedLesson);
  }

  @Test
  void createLesson_shouldThrowIfCourseNotFound() {
    UUID courseId = UUID.randomUUID();
    LessonRequestDto requestDto = new LessonRequestDto("Title", 60, courseId);

    when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> lessonService.createLesson(requestDto));
  }

  @Test
  void updateLesson_shouldUpdateAndReturnDto() {
    UUID lessonId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    LessonRequestDto requestDto = new LessonRequestDto("Updated", 90, courseId);
    Lesson lesson = new Lesson();
    Course course = new Course();
    Lesson updatedLesson = new Lesson();
    LessonResponseDto responseDto =
        new LessonResponseDto(lessonId, "Updated", 90, courseId);

    when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    when(lessonRepository.save(lesson)).thenReturn(updatedLesson);
    when(lessonMapper.toResponseDto(updatedLesson)).thenReturn(responseDto);

    LessonResponseDto result = lessonService.updateLesson(lessonId, requestDto);

    assertEquals("Updated", lesson.getTitle());
    assertEquals(90, lesson.getDuration());
    assertEquals(course, lesson.getCourse());
    assertEquals(responseDto, result);
    verify(lessonRepository).save(lesson);
    verify(lessonMapper).toResponseDto(updatedLesson);
  }

  @Test
  void updateLesson_shouldThrowIfLessonNotFound() {
    UUID lessonId = UUID.randomUUID();
    LessonRequestDto requestDto = new LessonRequestDto("Title", 60, UUID.randomUUID());

    when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> lessonService.updateLesson(lessonId, requestDto));
  }

  @Test
  void updateLesson_shouldThrowIfCourseNotFound() {
    UUID lessonId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    Lesson lesson = new Lesson();
    LessonRequestDto requestDto = new LessonRequestDto("Title", 60, courseId);

    when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
    when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> lessonService.updateLesson(lessonId, requestDto));
  }

  @Test
  void deleteLesson_shouldDeleteIfExists() {
    UUID lessonId = UUID.randomUUID();
    Lesson lesson = new Lesson();

    when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

    lessonService.deleteLesson(lessonId);

    verify(lessonRepository).delete(lesson);
  }

  @Test
  void deleteLesson_shouldThrowIfLessonNotFound() {
    UUID lessonId = UUID.randomUUID();
    when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> lessonService.deleteLesson(lessonId));
  }
}
