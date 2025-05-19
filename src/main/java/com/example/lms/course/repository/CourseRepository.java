package com.example.lms.course.repository;

import com.example.lms.course.model.Course;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, UUID> {

  Optional<Course> findByTitle(String title);

  Optional<Course> findByPrice(BigDecimal price);

}
