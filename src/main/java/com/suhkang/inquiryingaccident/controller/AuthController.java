package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.response.JwtAuthenticationResponse;
import com.suhkang.inquiryingaccident.object.request.LoginRequest;
import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.request.RefreshTokenRequest;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
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
  private final MemberRepository memberRepository;

  @PostMapping("/login")
  @LogMethodInvocation
  public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
    // 로그인 시에는 username(닉네임)과 password를 사용
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String accessToken = jwtTokenProvider.generateToken(authentication);
    String refreshToken = jwtTokenProvider.generateToken(authentication);
    return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));
  }

  @PostMapping("/signup")
  @LogMethodInvocation
  public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
    authService.signup(signupRequest);
    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/refresh")
  @LogMethodInvocation
  public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    return authService.findByRefreshToken(requestRefreshToken)
        .flatMap(authService::verifyExpiration)
        .map(token -> {
          // 리프레시 토큰이 유효하면, 해당 회원 정보로 새 액세스 토큰 발급
          Member member = memberRepository.findById(token.getMemberId())
              .orElseThrow(() -> new RuntimeException("Member not found"));
          // CustomUserDetails를 통해 권한 정보를 가져옴
          CustomUserDetails userDetails = new CustomUserDetails(member);
          Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          String newAccessToken = jwtTokenProvider.generateToken(authentication);
          // 필요에 따라 새 리프레시 토큰도 발급 (기존 토큰 교체)
          String newRefreshToken = authService.createRefreshToken(member).getToken();
          return ResponseEntity.ok(new JwtAuthenticationResponse(newAccessToken, newRefreshToken));
        })
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }
}
