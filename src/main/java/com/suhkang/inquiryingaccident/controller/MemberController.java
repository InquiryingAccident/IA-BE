package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.log.LogMethodInvocation;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.IsEmailAvailableRequest;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import com.suhkang.inquiryingaccident.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Tag(
    name = "회원 관리 API",
    description = "회원 관리 API 제공"
)
public class MemberController implements MemberControllerDocs {

  private final MemberService memberService;

  @PostMapping("/my-info")
  @LogMethodInvocation
  public ResponseEntity<MyInfoResponse> myInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    return ResponseEntity.ok(memberService.myInfo(customUserDetails.getMember()));
  }

  @DeleteMapping("/withdraw")
  @LogMethodInvocation
  public ResponseEntity<Void> withdraw(
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    memberService.withdraw(customUserDetails.getMember().getMemberId());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping(value = "/check-email", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<Boolean> isEmailAvailable(
      @ModelAttribute IsEmailAvailableRequest request) {
    return ResponseEntity.ok(memberService.isEmailAvailable(request));
  }
}
