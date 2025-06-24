package com.example.lms.lesson.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "video_lesson")
@Getter
@Setter
public class VideoLesson extends Lesson {

  private String url;

  private String platform;
}
