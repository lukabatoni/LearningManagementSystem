package com.example.lms.lesson.repository;

import com.example.lms.lesson.model.Lesson;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
  @NotNull Page<Lesson> findAll (@NotNull Pageable pageable);
}
