package com.suhkang.inquiryingaccident.object.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class RefreshAccessTokenByRefreshTokenRequest {
  @Schema(defaultValue = "exampleRefreshToken")
  private String refreshToken;
}
