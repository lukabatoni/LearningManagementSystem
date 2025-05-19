package com.example.lms.lesson.service;

import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.lesson.dto.LessonRequestDto;
import com.example.lms.lesson.dto.LessonResponseDto;
import com.example.lms.lesson.mapper.LessonMapper;
import com.example.lms.lesson.model.Lesson;
import com.example.lms.lesson.repository.LessonRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonService {
  private final LessonRepository lessonRepository;
  private final LessonMapper lessonMapper;
  private final CourseRepository courseRepository;

  @Transactional
  public LessonResponseDto createLesson(LessonRequestDto requestDto) {
    Course course = courseRepository.findById(requestDto.courseId())
        .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + requestDto.courseId()));

    Lesson lesson = lessonMapper.toEntity(requestDto);
    lesson.setCourse(course);

    var savedLesson = lessonRepository.save(lesson);
    return lessonMapper.toResponseDto(savedLesson);
  }

  public List<LessonResponseDto> getAllLessons() {
    var lessons = lessonRepository.findAll();
    return lessons.stream()
        .map(lessonMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public LessonResponseDto updateLesson(UUID id, LessonRequestDto requestDto) {
    Lesson lesson = lessonRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + id));

    Course course = courseRepository.findById(requestDto.courseId())
        .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + requestDto.courseId()));

    lesson.setTitle(requestDto.title());
    lesson.setDuration(requestDto.duration());
    lesson.setCourse(course);

    Lesson updatedLesson = lessonRepository.save(lesson);
    return lessonMapper.toResponseDto(updatedLesson);
  }

  @Transactional
  public void deleteLesson(UUID id) {
    Lesson lesson = lessonRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + id));
    lessonRepository.delete(lesson);
  }

}
