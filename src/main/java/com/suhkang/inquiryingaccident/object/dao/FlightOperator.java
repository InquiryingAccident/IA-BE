package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FlightOperator {

  // 운항사의 ICAO 코드 (예: "AAL")
  private String icao;

  // 운항사의 IATA 코드 (예: "AA")
  private String iata;

  // 운항사 이름 (예: "American Airlines")
  private String name;
}
