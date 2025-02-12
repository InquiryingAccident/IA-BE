package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccidentDBYearStatus extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false, unique = true)
  private int year;

  // 웹사이트에서 가져온 건수 (혹은 파싱 시도한 건수)
  private int totalRecords;

  private int successCount;

  private int failureCount;

  // 한번이라도 파싱 시도가 이루어졌다면 complete=true (실패건도 포함하여 파싱 시도한 경우)
  private boolean complete;

  private LocalDateTime lastUpdated;
}
