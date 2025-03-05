package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@SuperBuilder
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @CreatedDate
  @jakarta.persistence.Column(nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @jakarta.persistence.Column(nullable = false)
  private LocalDateTime updatedDate;

  @Builder.Default
  private Boolean isEdited = false;

  @PreUpdate
  public void beforeUpdate() {
    if (!createdDate.isEqual(updatedDate)) {
      this.isEdited = true;
    }
  }
}