package com.suhkang.inquiryingaccident.object.request;

import com.suhkang.inquiryingaccident.object.constants.SocialPlatform;
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
public class SocialLoginRequest {

  @Schema(defaultValue = "test")
  private String email;

  @Schema(defaultValue = "APPLE")
  private SocialPlatform socialPlatform;

  @Schema(defaultValue = "애플전용코드")
  private String socialPlatformId;
}