package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Flight extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID flightId;

  // 항공편 번호 (예: IATA/IATA 코드 조합)
  private String ident;

  // FlightAware 고유 식별자 (타임스탬프 포함)
  private String faFlightId;

  // 항공기 모델 코드 정보
  @ManyToOne(cascade = CascadeType.PERSIST)
  private AircraftType aircraftType;

  // 항공기 등록번호
  private String registration;

  // 운항사 정보 (ICAO, IATA, 이름)
  @Embedded
  private FlightOperator operator;
}
