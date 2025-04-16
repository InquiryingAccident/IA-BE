package com.suhkang.inquiryingaccident.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SocialPlatform {
  KAKAO("카카오"),
  NAVER("네이버"),
  APPLE("애플"),
  GOOGLE("구글"),
  FACEBOOK("페이스북"),
  TWITTER("트위터"),
  INSTAGRAM("인스타그램"),
  LINKEDIN("링크드인"),
  GITHUB("깃허브"),
  DISCORD("디스코드"),
  SLACK("슬랙"),
  ;
  private String description;
}
