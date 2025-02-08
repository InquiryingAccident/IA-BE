package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.constants.AccountStatus;
import com.suhkang.inquiryingaccident.object.constants.JwtTokenType;
import com.suhkang.inquiryingaccident.object.constants.Role;
import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.dao.RefreshToken;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.LoginRequest;
import com.suhkang.inquiryingaccident.object.request.RefreshAccessTokenByRefreshTokenRequest;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.object.response.LoginResponse;
import com.suhkang.inquiryingaccident.object.response.RefreshAccessTokenByRefreshTokenResponse;
import com.suhkang.inquiryingaccident.object.response.SignUpResponse;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.repository.RefreshTokenRepository;
import com.suhkang.inquiryingaccident.util.JwtTokenProvider;
import com.suhkang.inquiryingaccident.util.exception.CustomException;
import com.suhkang.inquiryingaccident.util.exception.ErrorCode;
import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

  public SignUpResponse signup(SignupRequest request) {

    // 이메일 검증
    if (memberRepository.existsByEmail(request.getEmail())) {
      log.error("회원 이메일이 이미 존재합니다 : {}", request.getEmail());
      throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
    }

    // 닉네임 검증
    if (memberRepository.existsByNickname(request.getNickname())) {
      log.error("회원 닉네임이 이미 존재합니다 : {}", request.getNickname());
      throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
    }

    Member member = Member.builder()
        .email(request.getEmail())
        .nickname(request.getNickname())
        .password(passwordEncoder.encode(request.getPassword()))
        .roles(Set.of(Role.ROLE_USER))
        .accountStatus(AccountStatus.ACTIVE)
        .build();

    Member savedMember = memberRepository.save(member);

    return SignUpResponse.builder()
        .member(savedMember)
        .build();
  }

  public LoginResponse login(LoginRequest request) {
    // Authentication 객체 생성: 이메일/패시워드 -> principal/credentials
    // -> DaoAuthenticationProviders -> UserDetailService.loadUserByUsername(principal) -> UserDetails 반환
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String accessToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.ACCESS);
    String refreshToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.REFRESH);

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public RefreshAccessTokenByRefreshTokenResponse refreshAccessTokenByRefreshToken(
      RefreshAccessTokenByRefreshTokenRequest request
  ) {
    // refreshToken 검증
    RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

    // refreshToken 만료시 삭제 -> 만료된 리프레시 에러
    if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(refreshToken);
      throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }

    // refreshToken -> memberID 추출 -> 새로운 accessToken 생성
    Member member = memberRepository.findById(jwtTokenProvider.getMemberIdFromToken(request.getRefreshToken()))
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    CustomUserDetails userDetails = new CustomUserDetails(member);
    Authentication authentication
        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    String newAccessToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.ACCESS);

    return RefreshAccessTokenByRefreshTokenResponse.builder()
        .accessToken(newAccessToken)
        .build();
  }

}
