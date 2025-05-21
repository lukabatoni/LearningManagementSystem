package com.example.lms.course.schedule;

import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.mail.MailtrapEmailService;
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
  private final MailtrapEmailService mailtrapEmailService;

  private static final String COURSE_REMINDER_SUBJECT = "Course Reminder";
  private static final String COURSE_REMINDER_MESSAGE = "Your course starts tomorrow!";

  @Scheduled(fixedRate = 120000, initialDelay = 100000)
  public void getAllTomorrowCourses() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    LocalDateTime startOfDay = tomorrow.atStartOfDay();
    LocalDateTime endOfDay = tomorrow.atTime(23, 59, 59);

    List<Course> courses = courseRepository
        .findByStartDateBetween(startOfDay, endOfDay);
    log.info("Found {} courses starting tomorrow", courses.size());

    List<String> emails = courseRepository
        .findStudentEmailsForCoursesStartingBetween(startOfDay, endOfDay);
    log.info("Found {} students enrolled in courses starting tomorrow", emails.size());

    for (String email : emails) {
      mailtrapEmailService.send(email, COURSE_REMINDER_SUBJECT, COURSE_REMINDER_MESSAGE);
    }
  }
}
