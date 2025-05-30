package com.suhkang.inquiryingaccident.object.response;

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
public class LoginResponse {

  private String accessToken;

  private String refreshToken;

  private Boolean isFirstLogin;
}