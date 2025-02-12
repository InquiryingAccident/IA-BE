package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.docs.ApiChangeLog;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLogs;
import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.SearchAccidentInfoRequest;
import com.suhkang.inquiryingaccident.object.response.SearchAccidentInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface PlaneAccidentControllerDocs {

  @ApiChangeLogs({
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
        - **`없음`**

        ## 반환값 (MyInfoResponse)
        - **`member`**: 회원 정보
        """
  )
  public ResponseEntity<SearchAccidentInfoResponse> searchAccidentInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute SearchAccidentInfoRequest request);
}
