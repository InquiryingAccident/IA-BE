package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.dao.RefreshToken;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.object.constants.AccountStatus;
import com.suhkang.inquiryingaccident.object.constants.Role;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.repository.RefreshTokenRepository;
import com.suhkang.inquiryingaccident.util.exception.CustomException;
import com.suhkang.inquiryingaccident.util.exception.ErrorCode;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  // refresh 토큰 만료 시간 (밀리초)
  @Value("${jwt.refresh-exp-time}")
  private long refreshTokenValidityInMilliseconds;

  public void signup(SignupRequest signupRequest) {
    if (memberRepository.existsByNickname(signupRequest.getUsername())) {
      throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
    }
    Member member = Member.builder()
        .nickname(signupRequest.getUsername())
        .password(passwordEncoder.encode(signupRequest.getPassword()))
        .roles(Set.of(Role.ROLE_USER))
        .accountStatus(AccountStatus.ACTIVE)
        .build();
    memberRepository.save(member);
  }

  // 회원 로그인 시 새 리프레시 토큰 생성 (기존 토큰이 있다면 삭제)
  public RefreshToken createRefreshToken(Member member) {
    // 기존에 해당 회원의 리프레시 토큰이 있으면 삭제
    refreshTokenRepository.findByMemberId(member.getMemberId())
        .ifPresent(refreshTokenRepository::delete);

    String token = UUID.randomUUID().toString();
    Instant expiryDate = Instant.now().plusMillis(refreshTokenValidityInMilliseconds);
    RefreshToken refreshToken = RefreshToken.builder()
        .token(token)
        .memberId(member.getMemberId())
        .memberEmail(member.getEmail())
        .expiryDate(expiryDate)
        .build();
    return refreshTokenRepository.save(refreshToken);
  }

  // refresh 토큰으로 DB에서 조회
  public Optional<RefreshToken> findByRefreshToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  // 만료 여부를 검사하여 만료된 경우 삭제
  public Optional<RefreshToken> verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      return Optional.empty();
    }
    return Optional.of(token);
  }

  public void deleteByMemberId(UUID memberId) {
    refreshTokenRepository.findByMemberId(memberId)
        .ifPresent(refreshTokenRepository::delete);
  }
}
