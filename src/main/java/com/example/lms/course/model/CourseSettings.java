package com.example.lms.course.model;

import com.example.lms.base.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_settings")
public class CourseSettings extends BaseEntity {

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(name = "is_public")
  private Boolean isPublic;
}
