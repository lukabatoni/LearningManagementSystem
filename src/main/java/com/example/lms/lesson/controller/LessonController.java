package com.example.lms.lesson.controller;

import com.example.lms.lesson.dto.LessonRequestDto;
import com.example.lms.lesson.dto.LessonResponseDto;
import com.example.lms.lesson.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lessons")
@Tag(name = "Lesson Management", description = "Endpoints for managing lessons")
public class LessonController {
  private final LessonService lessonService;

  @Operation(
      summary = "Create a new lesson",
      description = "Creates a new lesson with the provided details"
  )
  @PostMapping
  public ResponseEntity<LessonResponseDto> createLesson(@RequestBody @Valid LessonRequestDto requestDto) {
    LessonResponseDto response = lessonService.createLesson(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Get all lessons",
      description = "Retrieves a list of all lessons"
  )
  @GetMapping
  public ResponseEntity<Page<LessonResponseDto>> getAllLessons(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "2") int pageSize,
      @RequestParam(defaultValue = "created") String sortBy,
      @RequestParam(defaultValue = "desc") String direction
  ) {
    Page<LessonResponseDto> lessons = lessonService.getAllLessons(page, pageSize, sortBy, direction);
    return ResponseEntity.ok(lessons);
  }

  @Operation(
      summary = "Update a lesson",
      description = "Updates the details of an existing lesson"
  )
  @PutMapping("/{id}")
  public ResponseEntity<LessonResponseDto> updateLesson(@PathVariable UUID id,
                                                        @RequestBody @Valid LessonRequestDto requestDto) {
    LessonResponseDto response = lessonService.updateLesson(id, requestDto);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Delete a lesson",
      description = "Deletes a lesson by ID"
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
    lessonService.deleteLesson(id);
    return ResponseEntity.noContent().build();
  }

}
