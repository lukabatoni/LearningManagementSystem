package com.example.lms.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID id;

  @CreatedDate
  @Column(name = "created", updatable = false)
  private LocalDateTime created;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @LastModifiedDate
  @Column(name = "last_changed")
  private LocalDateTime lastChanged;

  @LastModifiedBy
  @Column(name = "last_changed_by")
  private String lastChangedBy;

}
