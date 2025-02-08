package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.util.exception.CustomException;
import com.suhkang.inquiryingaccident.util.exception.ErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  // 로그인 시 사용 (nickname 로 체크)
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    return new CustomUserDetails(member);
  }

  // JWT 토큰 검증 시 사용 (memberId 로 체크)
  public UserDetails loadUserByMemberId(UUID memberId) throws UsernameNotFoundException {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    return new CustomUserDetails(member);
  }
}
