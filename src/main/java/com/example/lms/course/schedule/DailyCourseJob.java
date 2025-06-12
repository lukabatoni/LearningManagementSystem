package com.example.lms.course.schedule;

import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.mail.EmailDetails;
import com.example.lms.mail.EmailService;
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
  private final EmailService emailService;

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

    String[] emails = courseRepository
        .findStudentEmailsForCoursesStartingBetween(startOfDay, endOfDay)
        .toArray(new String[courses.size()]);

    EmailDetails sendData = EmailDetails.builder()
        .recipient(emails)
        .msgBody(COURSE_REMINDER_MESSAGE)
        .subject(COURSE_REMINDER_SUBJECT)
        .build();
    log.info("Sending course reminder emails to {} students", emails.length);
    emailService.sendEmail(sendData);
  }
}
