package com.suhkang.inquiryingaccident.object.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(defaultValue = "test")
  private String email;

  @Schema(defaultValue = "test123")
  private String password;

  @Schema(defaultValue = "testNickname")
  private String nickname;
}
