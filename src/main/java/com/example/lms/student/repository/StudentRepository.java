package com.example.lms.student.repository;

import com.example.lms.student.model.Student;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Student s WHERE s.id = :id")
  Optional<Student> findByIdForUpdate(@Param("id") UUID id);

  @NotNull Page<Student> findAll(@NotNull Pageable pageable);
}
