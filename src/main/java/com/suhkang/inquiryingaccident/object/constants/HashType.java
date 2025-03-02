package com.suhkang.inquiryingaccident.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HashType {
  GITHUB_ISSUES("관리되는 Gihub Issue 에 대한 전체 해시값"),
  AIRCRAFT_TYPE("항공기 모델에 대한 전체 해시값"),
  AIRCRAFT("항공기 정보에 대한 전체 해시값");

  private final String description;
}
