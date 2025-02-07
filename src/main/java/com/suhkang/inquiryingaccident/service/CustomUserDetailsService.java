package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.Member;
import com.suhkang.inquiryingaccident.object.CustomUserDetails;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
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

  // 로그인 시 사용 (nickname 기반)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByNickname(username)
        .orElseThrow(() -> new UsernameNotFoundException("Member not found with nickname: " + username));
    return new CustomUserDetails(member);
  }

  // JWT 토큰 검증 시 사용 (memberId 기반)
  public UserDetails loadUserByMemberId(String memberIdStr) throws UsernameNotFoundException {
    try {
      UUID memberId = UUID.fromString(memberIdStr);
      Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new UsernameNotFoundException("Member not found with id: " + memberIdStr));
      return new CustomUserDetails(member);
    } catch (IllegalArgumentException e) {
      throw new UsernameNotFoundException("Invalid member id format: " + memberIdStr);
    }
  }
}
