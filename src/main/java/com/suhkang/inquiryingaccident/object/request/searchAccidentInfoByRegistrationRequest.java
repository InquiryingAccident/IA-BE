package com.suhkang.inquiryingaccident.object.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class searchAccidentInfoByRegistrationRequest {
  @Schema(defaultValue = "C-FPRP")
  private String registration;

  // 페이징 및 정렬 정보
  @Schema(defaultValue = "0")
  private int page = 0;
  @Schema(defaultValue = "10")
  private int size = 10;
  // Supported sort fields: wikibaseId, accidentDate, aircraftType, registration, operator, fatalities,
  // location, aircraftRegistrationCode, damage, hasPreliminaryReport
  @Schema(defaultValue = "accidentDate")
  private String sortField = "accidentDate";
  // Supported sort directions: ASC, DESC (기본값: DESC)
  @Schema(defaultValue = "DESC")
  private String sortDirection = "DESC";
}
