package com.example.lms.lesson.repository;

import com.example.lms.lesson.model.Lesson;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
}
