package com.suhkang.inquiryingaccident.object.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightInfoByFlightAwareApiRequest {
  // 항공편 번호 (예: "AA123")
  private String flightNumber;
}
