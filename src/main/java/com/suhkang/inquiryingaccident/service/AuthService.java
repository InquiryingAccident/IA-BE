package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.global.exception.CustomException;
import com.suhkang.inquiryingaccident.global.exception.ErrorCode;
import com.suhkang.inquiryingaccident.global.util.JwtTokenProvider;
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
import java.time.Instant;
import java.time.LocalDateTime;
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

    // 활성 회원 중 email 중복 체크
    if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
    }
    // nickname 중복 체크
    if (memberRepository.findByNickname(request.getNickname()).isPresent()) {
      throw new CustomException(ErrorCode.NICKNAME_DUPLICATION);
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
    // Authentication -> 인증 처리
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String accessToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.ACCESS);
    String refreshToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.REFRESH);

    // 현재 인증된 회원 정보 가져오기
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Member member = userDetails.getMember();

    // 마지막 로그인 시간 업데이트
    member.setLastLoginTime(LocalDateTime.now());
    memberRepository.save(member);

    // RefreshToken 엔티티 생성 및 저장
    RefreshToken refreshTokenEntity = RefreshToken.builder()
        .token(refreshToken)
        .memberId(member.getMemberId())
        .memberEmail(member.getEmail())
        .expiryDate(Instant.now().plusMillis(JwtTokenType.REFRESH.getDurationMilliseconds()))
        .build();
    refreshTokenRepository.save(refreshTokenEntity);

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public RefreshAccessTokenByRefreshTokenResponse refreshAccessTokenByRefreshToken(
      RefreshAccessTokenByRefreshTokenRequest request
  ) {
    // 저장소에서 refreshToken 엔티티 조회
    RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

    // refreshToken 회전 방식: 유효 여부와 상관없이 항상 새로운 토큰 발급
    if (RefreshToken.isExpired(refreshTokenEntity)) {
      log.info("만료된 토큰이 전달되었습니다. 자동 로그인을 위해 토큰을 갱신합니다. memberEmail: {}", refreshTokenEntity.getMemberEmail());
    } else {
      log.info("유효한 토큰이 전달되었습니다. 자동 로그인을 위해 토큰을 갱신합니다. memberEmail: {}", refreshTokenEntity.getMemberEmail());
    }

    // 저장된 memberId를 사용하여 회원 조회
    Member member = memberRepository.findById(refreshTokenEntity.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    CustomUserDetails userDetails = new CustomUserDetails(member);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    // 새로운 accessToken과 refreshToken 생성
    String newAccessToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.ACCESS);
    String newRefreshToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.REFRESH);

    // refreshToken 엔티티 업데이트: 새로운 토큰 값과 만료시간 적용
    refreshTokenEntity.setToken(newRefreshToken);
    refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(JwtTokenType.REFRESH.getDurationMilliseconds()));
    refreshTokenRepository.save(refreshTokenEntity);

    log.info("새로운 액세스 토큰, 리프레시 토큰이 발급되었습니다. memberEmail: {}", member.getEmail());

    return RefreshAccessTokenByRefreshTokenResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }


}
