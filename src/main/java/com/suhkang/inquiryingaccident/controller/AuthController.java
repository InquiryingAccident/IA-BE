package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.request.LoginRequest;
import com.suhkang.inquiryingaccident.object.request.RefreshAccessTokenByRefreshTokenRequest;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.object.response.LoginResponse;
import com.suhkang.inquiryingaccident.object.response.RefreshAccessTokenByRefreshTokenResponse;
import com.suhkang.inquiryingaccident.object.response.SignUpResponse;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.service.AuthService;
import com.suhkang.inquiryingaccident.util.log.LogMethodInvocation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final MemberRepository memberRepository;

  @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<LoginResponse> login(
      @ModelAttribute LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

  @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<SignUpResponse> signup(
      @ModelAttribute SignupRequest signupRequest) {
    return ResponseEntity.ok(authService.signup(signupRequest));
  }

  @PostMapping(value = "/refresh", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMethodInvocation
  public ResponseEntity<RefreshAccessTokenByRefreshTokenResponse> refreshAccessTokenByRefreshToken(
      @ModelAttribute RefreshAccessTokenByRefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshAccessTokenByRefreshToken(request));
  }
}
