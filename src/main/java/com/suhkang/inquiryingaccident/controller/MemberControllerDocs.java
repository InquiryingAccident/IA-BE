package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLog;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLogs;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface MemberControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.15",
          author = Author.SUHSAECHAN,
          issueNumber = 10,
          description = "반환값 필드 분리: email, nickname, accountStatus, createDate, lastLoginTime"
      ),
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "내 정보 조회 기능 구현"
      )
  })
  @Operation(
      summary = "내 정보 조회",
      description = """
        ## 인증(JWT): **필요**

        ## 요청 파라미터
        - **없음**

        ## 반환값 (MyInfoResponse)
        - **email**: 회원 이메일 주소
        - **nickname**: 회원 닉네임
        - **accountStatus**: 회원 계정 상태
        - **createDate**: 회원 가입일자
        - **lastLoginTime**: 마지막 로그인 시간
        """
  )
  ResponseEntity<MyInfoResponse> myInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails);
}
