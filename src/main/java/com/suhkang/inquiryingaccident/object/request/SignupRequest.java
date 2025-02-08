package com.suhkang.inquiryingaccident.object.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
  private String email;
  private String password;
  private String nickname;
}
