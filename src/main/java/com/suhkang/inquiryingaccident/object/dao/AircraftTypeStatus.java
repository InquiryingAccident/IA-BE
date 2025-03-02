package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AircraftTypeStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  // 웹사이트에서 가져온 AircraftType 총 개수
  @Column(nullable = false)
  private int totalRecords;

  // 성공적으로 저장된 건수
  private int successCount;

  // 실패한 건수
  private int failureCount;

  // 파싱이 완료되었는지 여부 (실패 포함)
  private boolean complete;

  // 마지막 업데이트 시점
  private LocalDateTime lastUpdated;
}