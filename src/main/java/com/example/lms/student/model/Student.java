package com.example.lms.student.model;

import com.example.lms.base.model.BaseEntity;
import com.example.lms.course.model.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student extends BaseEntity {

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Column(name = "coins")
  private BigDecimal coins;

  @ManyToMany(mappedBy = "students")
  private Set<Course> courses = new HashSet<>();

}
