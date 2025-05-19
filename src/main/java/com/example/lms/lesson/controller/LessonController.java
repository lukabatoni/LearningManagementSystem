package com.example.lms.lesson.controller;

import com.example.lms.lesson.dto.LessonRequestDto;
import com.example.lms.lesson.dto.LessonResponseDto;
import com.example.lms.lesson.service.LessonService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lessons")
public class LessonController {
  private final LessonService lessonService;

  @PostMapping
  public LessonResponseDto createLesson(@RequestBody LessonRequestDto requestDto) {
    return lessonService.createLesson(requestDto);
  }

  @GetMapping
  public List<LessonResponseDto> getAllLessons() {
    return lessonService.getAllLessons();
  }

  @PutMapping("/{id}")
  public LessonResponseDto updateLesson(@PathVariable UUID id, @RequestBody LessonRequestDto requestDto) {
    return lessonService.updateLesson(id, requestDto);
  }

  @DeleteMapping("/{id}")
  public void deleteLesson(@PathVariable UUID id) {
    lessonService.deleteLesson(id);
  }

}
