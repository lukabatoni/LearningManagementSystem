package com.example.lms.student.repository;

import com.example.lms.student.model.Student;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, UUID> {

  Optional<Student> findByEmail(String email);

  Optional<Student> findByCoins(BigDecimal coins);

}