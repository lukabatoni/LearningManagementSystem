package com.example.lms.lesson.service;

import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.lesson.dto.LessonRequestDto;
import com.example.lms.lesson.dto.LessonResponseDto;
import com.example.lms.lesson.mapper.LessonMapper;
import com.example.lms.lesson.model.Lesson;
import com.example.lms.lesson.repository.LessonRepository;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonService {
  private final LessonRepository lessonRepository;
  private final LessonMapper lessonMapper;
  private final CourseRepository courseRepository;

  private static final String LESSON_NOT_FOUND = "Lesson not found with id: ";
  private static final String COURSE_NOT_FOUND = "Course not found with id: ";

  @Transactional
  @CacheEvict(value = "lessons", allEntries = true)
  public LessonResponseDto createLesson(@NonNull final LessonRequestDto requestDto) {
    Course course = courseRepository.findById(requestDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND + requestDto.courseId()));

    Lesson lesson = lessonMapper.toEntity(requestDto);
    lesson.setCourse(course);

    var savedLesson = lessonRepository.save(lesson);
    return lessonMapper.toResponseDto(savedLesson);
  }

  @Cacheable(value = "lessons")
  public Page<LessonResponseDto> getAllLessons(int page, int pageSize, String sortBy, String direction) {
    Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, pageSize, sort);
    Page<Lesson> lessonPage = lessonRepository.findAll(pageable);
    return lessonPage.map(lessonMapper::toResponseDto);
  }

  @Transactional
  public LessonResponseDto updateLesson(@NonNull final UUID id,
                                        @NonNull final LessonRequestDto requestDto) {
    Lesson lesson = lessonRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(LESSON_NOT_FOUND + id));

    Course course = courseRepository.findById(requestDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND + requestDto.courseId()));

    lesson.setTitle(requestDto.title());
    lesson.setDuration(requestDto.duration());
    lesson.setCourse(course);

    Lesson updatedLesson = lessonRepository.save(lesson);
    return lessonMapper.toResponseDto(updatedLesson);
  }

  @Transactional
  public void deleteLesson(@NonNull final UUID id) {
    Lesson lesson = lessonRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(LESSON_NOT_FOUND + id));
    lessonRepository.delete(lesson);
  }

}
