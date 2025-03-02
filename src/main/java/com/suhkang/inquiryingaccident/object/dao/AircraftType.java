package com.suhkang.inquiryingaccident.object.dao;

import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AircraftType{
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID aircraftTypeId;

  // 항공기 모델 코드 (예: "B737")
  @Column(length = 10, unique = true, nullable = false)
  private String modelCode;

  // 제조사 (예: "Boeing")
  private String manufacturer;

  // 모델명 (예: "737-700")
  private String modelName;

  // 첫 비행 연도 (예: 1997)
  private Integer firstFlightYear;

  // 상세 설명 또는 카테고리 (예: "twin-jet airliner")
  @Column(length = 255)
  private String description;

  // 항공기 사용 정보 (상업, 비즈니스)
  @Enumerated(EnumType.STRING)
  private AircraftUsageType aircraftUsageType;

  // ASN 항공기 사고내역 정보 URL
  private String asnAccidentUrl;
}
