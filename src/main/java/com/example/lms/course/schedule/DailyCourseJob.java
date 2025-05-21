package com.example.lms.course.schedule;

import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyCourseJob {
  private final CourseRepository courseRepository;

  @Scheduled(fixedRate = 120000)
  public void getAllTomorrowCourses() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    LocalDateTime startOfDay = tomorrow.atStartOfDay();
    LocalDateTime endOfDay = tomorrow.atTime(23, 59, 59);

    List<Course> courses = courseRepository
        .findByStartDateBetween(startOfDay, endOfDay);
    log.info("Found {} courses starting tomorrow", courses.size());
  }
}
