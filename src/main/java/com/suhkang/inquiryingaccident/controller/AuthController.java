package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.LoginRequest;
import com.suhkang.inquiryingaccident.object.request.LogoutRequest;
import com.suhkang.inquiryingaccident.object.request.RefreshAccessTokenByRefreshTokenRequest;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.object.request.SocialLoginRequest;
import com.suhkang.inquiryingaccident.object.response.LoginResponse;
import com.suhkang.inquiryingaccident.object.response.RefreshAccessTokenByRefreshTokenResponse;
import com.suhkang.inquiryingaccident.object.response.SignUpResponse;
import com.suhkang.inquiryingaccident.object.response.SocialLoginResponse;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.suhsaechan.suhlogger.annotation.LogMonitor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
    name = "인증 관리 API",
    description = "인증 관리 API 제공"
)
public class AuthController implements AuthControllerDocs{

  private final AuthService authService;
  private final MemberRepository memberRepository;

  @Override
  @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitor
  public ResponseEntity<LoginResponse> login(
      @ModelAttribute LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

  @Override
  @PostMapping(value = "/social-login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitor
  public ResponseEntity<SocialLoginResponse> socialLogin(
      @ModelAttribute SocialLoginRequest socialLoginRequest) {
    return ResponseEntity.ok(authService.SocialLogin(socialLoginRequest));
  }

  @Override
  @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitor
  public ResponseEntity<SignUpResponse> signup(
      @ModelAttribute SignupRequest signupRequest) {
    authService.signup(signupRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  @PostMapping(value = "/refresh", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitor
  public ResponseEntity<RefreshAccessTokenByRefreshTokenResponse> refreshAccessTokenByRefreshToken(
      @ModelAttribute RefreshAccessTokenByRefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshAccessTokenByRefreshToken(request));
  }

  @PostMapping(value = "/logout", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitor
  public ResponseEntity<Void> logout(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute LogoutRequest request) {
    request.setMember(customUserDetails.getMember());
    authService.logout(request);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}