package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.log.LogMethodInvocation;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.SearchAccidentInfoRequest;
import com.suhkang.inquiryingaccident.object.request.searchAccidentInfoByRegistrationRequest;
import com.suhkang.inquiryingaccident.object.response.SearchAccidentInfoResponse;
import com.suhkang.inquiryingaccident.object.response.searchAccidentInfoByRegistrationResponse;
import com.suhkang.inquiryingaccident.service.PlaneAccidentService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/plane-accident")
@Tag(
    name = "비행기 사고 관리 API",
    description = "비행기 사고 관리 API 제공"
)
public class PlaneAccidentController implements PlaneAccidentControllerDocs{

  private final PlaneAccidentService planeAccidentService;

  @PostMapping(value = "/filter/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<SearchAccidentInfoResponse> searchAccidentInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute SearchAccidentInfoRequest request
  ){
    return ResponseEntity.ok(planeAccidentService.searchAccidentInfo(request));
  }

  @PostMapping(value = "/registration/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<searchAccidentInfoByRegistrationResponse> searchAccidentInfoByRegistration(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute searchAccidentInfoByRegistrationRequest request
  ){
    return ResponseEntity.ok(planeAccidentService.searchAccidentInfoByRegistration(request));
  }

}
