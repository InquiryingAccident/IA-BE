package com.suhkang.inquiryingaccident.object.request;

import com.suhkang.inquiryingaccident.object.constants.AircraftRegistrationCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchAccidentInfoRequest {
  // 검색 조건 (값이 없으면 null)
  private String wikibaseId;
  private LocalDate accidentDate;
  private String aircraftType;
  private String registration;
  private String operator;
  private Integer fatalities;
  private String location;
  private AircraftRegistrationCode aircraftRegistrationCode; // Enum, 없으면 null
  private String damage;
  private Boolean hasPreliminaryReport;

  // 페이징 및 정렬 정보
  private int page = 0;
  private int size = 10;
  // Supported sort fields: wikibaseId, accidentDate, aircraftType, registration, operator, fatalities,
  // location, aircraftRegistrationCode, damage, hasPreliminaryReport
  private String sortField = "accidentDate";
  // Supported sort directions: ASC, DESC (기본값: DESC)
  private String sortDirection = "DESC";
}
