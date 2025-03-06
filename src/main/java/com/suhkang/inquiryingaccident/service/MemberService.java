package com.suhkang.inquiryingaccident.service;

import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.superLog;

import com.suhkang.inquiryingaccident.global.exception.CustomException;
import com.suhkang.inquiryingaccident.global.exception.ErrorCode;
import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.dao.RefreshToken;
import com.suhkang.inquiryingaccident.object.request.IsEmailAvailableRequest;
import com.suhkang.inquiryingaccident.object.request.LogoutRequest;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.repository.RefreshTokenRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public MyInfoResponse myInfo(Member member) {
    return MyInfoResponse.builder()
        .email(member.getEmail())
        .nickname(member.getNickname())
        .accountStatus(member.getAccountStatus())
        .createDate(member.getCreatedDate())
        .lastLoginTime(member.getLastLoginTime())
        .build();
  }

  @Transactional
  public void logout(LogoutRequest request) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    refreshTokenRepository.deleteById(refreshToken.getRefreshTokenId());

    superLog(refreshToken);
    lineLog(null);
    lineLog("회원: " + request.getMember().getEmail() + " 로그아웃 완료" );
    lineLog(null);
  }

  @Transactional
  public void withdraw(UUID memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    refreshTokenRepository.deleteByMemberId(memberId);
    memberRepository.delete(member);
  }

  @Transactional(readOnly = true)
  public boolean isEmailAvailable(IsEmailAvailableRequest request) {
    return memberRepository.findByEmail(request.getEmail()).isEmpty();
  }

}