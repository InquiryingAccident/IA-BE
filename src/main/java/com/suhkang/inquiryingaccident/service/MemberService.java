package com.suhkang.inquiryingaccident.service;

import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLogDebug;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.superLogDebug;

import com.suhkang.inquiryingaccident.global.exception.CustomException;
import com.suhkang.inquiryingaccident.global.exception.ErrorCode;
import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.request.IsEmailAvailableRequest;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.repository.RefreshTokenRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public MyInfoResponse myInfo(Member member) {
    log.info("내 정보 조회 시작 - memberId: {}", member.getMemberId());
    lineLog("Building member info response");

    MyInfoResponse response = MyInfoResponse.builder()
        .email(member.getEmail())
        .nickname(member.getNickname())
        .accountStatus(member.getAccountStatus())
        .createDate(member.getCreatedDate())
        .lastLoginTime(member.getLastLoginTime())
        .build();
    superLogDebug(response); // 응답 객체 출력

    log.info("내 정보 조회 완료 - memberId: {}", member.getMemberId());
    return response;
  }

  @Transactional
  public void withdraw(UUID memberId) {
    log.info("회원 탈퇴 요청 시작 - memberId: {}", memberId);
    lineLog("Finding member");

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> {
          log.warn("회원 조회 실패 - memberId: {}", memberId);
          return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        });
    superLogDebug(member); // 회원 객체 출력

    lineLog("Deleting refresh tokens");
    refreshTokenRepository.deleteByMemberId(memberId);
    log.debug("리프레시 토큰 삭제 완료 - memberId: {}", memberId);

    lineLog("Soft deleting member");
    memberRepository.delete(member); // Soft Delete (isDeleted = true)
    log.info("회원 탈퇴 완료 - memberId: {}", memberId);
  }

  @Transactional(readOnly = true)
  public boolean isEmailAvailable(IsEmailAvailableRequest request) {
    log.info("이메일 사용 가능 여부 확인 시작 - email: {}", request.getEmail());
    lineLogDebug("Checking email availability");

    boolean isAvailable = memberRepository.findByEmail(request.getEmail()).isEmpty();
    log.debug("이메일 사용 가능 여부 - email: {}, isAvailable: {}", request.getEmail(), isAvailable);

    log.info("이메일 사용 가능 여부 확인 완료 - email: {}", request.getEmail());
    return isAvailable;
  }
}