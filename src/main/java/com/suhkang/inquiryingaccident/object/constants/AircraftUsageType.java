package com.suhkang.inquiryingaccident.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AircraftUsageType {
  ALL("모든 유형"),
  COMMERCIAL("상업"),
  CORPORATE("비즈니스");

  private final String description;
}
