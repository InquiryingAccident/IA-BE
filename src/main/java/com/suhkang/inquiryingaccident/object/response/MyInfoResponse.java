package com.suhkang.inquiryingaccident.object.response;

import com.suhkang.inquiryingaccident.object.constants.AccountStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyInfoResponse {
  private String email;
  private String nickname;
  private AccountStatus accountStatus;
  private LocalDateTime createDate;    // 회원 가입일자
  private LocalDateTime lastLoginTime; // 마지막 로그인 시간
}