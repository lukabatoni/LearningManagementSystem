package com.example.lms.course.repository;

import com.example.lms.course.model.Course;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, UUID> {

  Optional<Course> findByTitle(String title);

  Optional<Course> findByPrice(BigDecimal price);

  @Query("SELECT c FROM Course c JOIN c.settings s " +
      "WHERE s.startDate BETWEEN :start AND :end")
  List<Course> findByStartDateBetween(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

}
