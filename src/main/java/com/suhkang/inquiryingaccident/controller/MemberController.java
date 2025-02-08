package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import com.suhkang.inquiryingaccident.service.MemberService;
import com.suhkang.inquiryingaccident.util.log.LogMethodInvocation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

  private final MemberService memberService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/my-info")
  @LogMethodInvocation
  public ResponseEntity<MyInfoResponse> myInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ){
    return ResponseEntity.ok(memberService.myInfo(customUserDetails.getMember()));
  }

}
