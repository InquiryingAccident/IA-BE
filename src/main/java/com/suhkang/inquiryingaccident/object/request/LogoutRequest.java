package com.suhkang.inquiryingaccident.object.request;

import com.suhkang.inquiryingaccident.object.dao.Member;
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
public class LogoutRequest {
  private Member member;
  private String refreshToken;
}
