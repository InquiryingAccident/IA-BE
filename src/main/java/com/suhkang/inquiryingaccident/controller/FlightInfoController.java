package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.log.LogMethodInvocation;
import com.suhkang.inquiryingaccident.object.request.FlightInfoByFlightAwareApiRequest;
import com.suhkang.inquiryingaccident.object.response.FlightInfoByFlightAwareApiResponse;
import com.suhkang.inquiryingaccident.service.FlightInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flight-info")
@Tag(name = "항공편 정보 API", description = "FlightAware API 연동을 통해 항공편 정보를 제공")
public class FlightInfoController {

  private final FlightInfoService flightInfoService;

  /**
   * FlightAware API를 호출하여 항공편 정보를 조회
   */
  @PostMapping(value = "/registration/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<FlightInfoByFlightAwareApiResponse> getFlightInfo(
      @ModelAttribute FlightInfoByFlightAwareApiRequest request) {

    FlightInfoByFlightAwareApiResponse response = flightInfoService.getFlightInfoByFlightAwareApi(request);
    return ResponseEntity.ok(response);
  }
}
