package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.Member;
import com.suhkang.inquiryingaccident.object.SignupRequest;
import com.suhkang.inquiryingaccident.object.constants.AccountStatus;
import com.suhkang.inquiryingaccident.object.constants.Role;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.util.exception.CustomException;
import com.suhkang.inquiryingaccident.util.exception.ErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

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
}
