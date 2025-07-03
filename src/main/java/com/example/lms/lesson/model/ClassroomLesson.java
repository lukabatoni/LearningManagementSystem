package com.example.lms.lesson.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "classroom_lesson")
@Getter
@Setter
public class ClassroomLesson extends Lesson {

  private String location;

  private Integer capacity;
}
