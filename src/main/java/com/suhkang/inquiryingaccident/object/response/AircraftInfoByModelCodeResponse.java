package com.suhkang.inquiryingaccident.object.response;

import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftInfoByModelCodeResponse {
  private String modelCode;
  private String manufacturer;
  private String modelName;
  private Integer firstFlightYear;
  private String description;
  private AircraftUsageType aircraftUsageType;
  private String asnAccidentUrl;
}