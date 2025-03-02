package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.log.LogMethodInvocation;
import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.AircraftInfoByModelCodeRequest;
import com.suhkang.inquiryingaccident.object.request.FlightInfoByFlightAwareApiRequest;
import com.suhkang.inquiryingaccident.object.response.FlightInfoByFlightAwareApiResponse;
import com.suhkang.inquiryingaccident.service.FlightInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plane")
@Tag(name = "항공편 및 항공기 정보 API", description = "FlightAware API 연동 및 항공기 정보 제공")
public class FlightInfoController implements FlightInfoControllerDocs{

  private final FlightInfoService flightInfoService;

  /**
   * FlightAware API를 호출하여 항공편 정보를 조회
   */
  @PostMapping(value = "/registration/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<FlightInfoByFlightAwareApiResponse> getFlightInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute FlightInfoByFlightAwareApiRequest request) {
    FlightInfoByFlightAwareApiResponse response = flightInfoService.getFlightInfoByFlightAwareApi(request);
    return ResponseEntity.ok(response);
  }

  /**
   * 항공기 코드로 항공기 정보 조회 (예: A319)
   */
  @PostMapping(value = "/type/model-code", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<Aircraft> getAircraftTypeInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute @Valid AircraftInfoByModelCodeRequest request){
    return ResponseEntity.ok(flightInfoService.getAircraftTypeInfo(request));
  }

  /**
   * 전체 항공기 코드 리스트 반환
   */
  @PostMapping(value = "/get/model-codes")
  @LogMethodInvocation
  public ResponseEntity<List<String>> getAllAircraftModelCodes(
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    return ResponseEntity.ok(flightInfoService.getAllAircraftModelCodes());
  }
}