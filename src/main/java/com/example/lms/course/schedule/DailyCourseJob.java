package com.example.lms.course.schedule;

import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.mail.EmailDetails;
import com.example.lms.mail.EmailService;
import com.example.lms.mail.EmailTemplateService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
  private final EmailTemplateService emailTemplateService;

  private static final String COURSE_REMINDER_SUBJECT = "Course Reminder";

  @Scheduled(fixedRate = 120000, initialDelay = 100000)
  public void getAllTomorrowCourses() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    LocalDateTime startOfDay = tomorrow.atStartOfDay();
    LocalDateTime endOfDay = tomorrow.atTime(23, 59, 59);

    List<Course> courses = courseRepository.findByStartDateBetween(startOfDay, endOfDay);
    log.info("Found {} courses starting tomorrow", courses.size());

    for (Course course : courses) {
      course.getStudents().forEach(student -> {
        try {
          String body = emailTemplateService.renderReminderTemplate(
              student.getLocale().name(),
              Map.of(
                  "firstName", student.getFirstName(),
                  "courseTitle", course.getTitle(),
                  "startDate", course.getSettings().getStartDate().toLocalDate()
              )
          );

          EmailDetails email = EmailDetails.builder()
              .recipient(new String[]{student.getEmail()})
              .subject(COURSE_REMINDER_SUBJECT)
              .msgBody(body)
              .build();

          emailService.sendEmail(email);
          log.info("Sent reminder to {} ({})", student.getEmail(), student.getLocale());

        } catch (Exception e) {
          log.error("Failed to send email to {}: {}", student.getEmail(), e.getMessage());
        }
      });
    }
  }
}
