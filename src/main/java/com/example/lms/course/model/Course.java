package com.example.lms.course.model;

import com.example.lms.base.model.BaseEntity;
import com.example.lms.lesson.model.Lesson;
import com.example.lms.student.model.Student;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course extends BaseEntity {

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "price")
  private BigDecimal price;

  @Column(name = "coins_paid")
  private BigDecimal coinsPaid;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "settings_id", referencedColumnName = "id")
  private CourseSettings settings;

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Lesson> lessons = new ArrayList<>();

  @ManyToMany()
  @JoinTable(
      name = "course_student",
      joinColumns = @JoinColumn(name = "course_id"),
      inverseJoinColumns = @JoinColumn(name = "student_id")
  )
  private Set<Student> students = new HashSet<>();

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getCoinsPaid() {
    return coinsPaid;
  }

  public void setCoinsPaid(BigDecimal coinsPaid) {
    this.coinsPaid = coinsPaid;
  }

  public CourseSettings getSettings() {
    return settings;
  }

  public void setSettings(CourseSettings settings) {
    this.settings = settings;
  }

  public Set<Student> getStudents() {
    return students;
  }

  public void setStudents(Set<Student> students) {
    this.students = students;
  }

  public List<Lesson> getLessons() {
    return lessons;
  }

  public void setLessons(List<Lesson> lessons) {
    this.lessons = lessons;
  }
}
