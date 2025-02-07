package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.JwtAuthenticationResponse;
import com.suhkang.inquiryingaccident.object.LoginRequest;
import com.suhkang.inquiryingaccident.object.SignupRequest;
import com.suhkang.inquiryingaccident.service.AuthService;
import com.suhkang.inquiryingaccident.util.JwtTokenProvider;
import com.suhkang.inquiryingaccident.util.log.LogMethodInvocation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthService authService;

  @PostMapping("/login")
  @LogMethodInvocation
  public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
    // 로그인 시에는 username(닉네임)과 password를 사용
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtTokenProvider.generateToken(authentication);
    return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
  }

  @PostMapping("/signup")
  @LogMethodInvocation
  public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
    authService.signup(signupRequest);
    return ResponseEntity.ok("User registered successfully");
  }
}
