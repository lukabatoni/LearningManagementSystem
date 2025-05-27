package com.example.lms.course.repository;

import com.example.lms.course.model.Course;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, UUID> {

  @Query("SELECT c FROM Course c JOIN c.settings s " +
      "WHERE s.startDate BETWEEN :start AND :end")
  List<Course> findByStartDateBetween(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  @Query(" select s.email from Course c " +
      " join c.students s " +
      " where c.settings.startDate between :start and :end")
  List<String> findStudentEmailsForCoursesStartingBetween(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);
}
