package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.docs.ApiChangeLog;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLogs;
import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.IsEmailAvailableRequest;
import com.suhkang.inquiryingaccident.object.request.LogoutRequest;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;

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

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.03.03",
          author = Author.SUHSAECHAN,
          issueNumber = 22,
          description = "로그아웃 API 추가"
      )
  })
  @Operation(
      summary = "로그아웃",
      description = """
            ## 인증(JWT): **필요**
            
            ## 요청 헤더
            - **`Authorization`**: "Bearer {refreshToken}" 형태로 Refresh Token 전달
            
            ## 반환값
            - 없음 (204 No Content)
            
            ## 에러코드
            - **`INVALID_REFRESH_TOKEN`**: 유효하지 않은 Refresh Token
            - **`UNAUTHORIZED`**: 인증 실패
            """
  )
  ResponseEntity<Void> logout(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute LogoutRequest request );

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.03.03",
          author = Author.SUHSAECHAN,
          issueNumber = 22,
          description = "회원탈퇴 API 추가 (Soft Delete 적용)"
      )
  })
  @Operation(
      summary = "회원탈퇴",
      description = """
            ## 인증(JWT): **필요**
            
            ## 요청 파라미터
            - 없음 (AuthenticationPrincipal에서 사용자 정보 추출)
            
            ## 반환값
            - 없음 (204 No Content)
            
            ## 에러코드
            - **`MEMBER_NOT_FOUND`**: 유저를 찾을 수 없음
            """
  )
  ResponseEntity<Void> withdraw(@AuthenticationPrincipal CustomUserDetails customUserDetails);

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.03.03",
          author = Author.SUHSAECHAN,
          issueNumber = 22,
          description = "이메일 중복 확인 API 추가"
      )
  })
  @Operation(
      summary = "이메일 사용 가능 여부 확인",
      description = """
        ## 인증(JWT): **불필요**
        
        ## 요청 파라미터 (IsEmailAvailableRequest)
        - **`email`**: 확인할 이메일 주소 (form-urlencoded 형식)
        
        ## 반환값
        - **`true`**: 이메일 사용 가능 (활성 회원 중 중복 없음)
        - **`false`**: 이메일 사용 불가 (활성 회원 중 중복 있음)
        
        ## 에러코드
        - 없음
        """
  )
  ResponseEntity<Boolean> isEmailAvailable(@ModelAttribute IsEmailAvailableRequest request);

}
