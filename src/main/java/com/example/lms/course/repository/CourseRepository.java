package com.example.lms.course.repository;

import com.example.lms.course.model.Course;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, UUID> {

  @EntityGraph(attributePaths = {"students", "settings"})
  @Query("SELECT c FROM Course c JOIN c.settings s " +
      "WHERE s.startDate BETWEEN :start AND :end")
  List<Course> findByStartDateBetween(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  @NotNull Page<Course> findAll (@NotNull Pageable pageable);
}
