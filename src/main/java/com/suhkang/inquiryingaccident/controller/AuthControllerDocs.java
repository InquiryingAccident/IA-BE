package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.LoginRequest;
import com.suhkang.inquiryingaccident.object.request.LogoutRequest;
import com.suhkang.inquiryingaccident.object.request.RefreshAccessTokenByRefreshTokenRequest;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.object.response.LoginResponse;
import com.suhkang.inquiryingaccident.object.response.RefreshAccessTokenByRefreshTokenResponse;
import com.suhkang.inquiryingaccident.object.response.SignUpResponse;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLog;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLogs;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface AuthControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "로그인 기능 구현"
      )
  })
  @Operation(
      summary = "로그인",
      description = """
        ## 인증(JWT): **불필요**
        
        ## 참고사항
        - **`email`**: 이메일 형식에 맞아야 합니다.
        - **`password`**: 비밀번호는 8자리 이상이어야 합니다.

        ## 요청 파라미터 (LoginRequest)
        - **`email`**: 이메일 주소
        - **`password`**: 비밀번호
                           
        ## 반환값 (LoginResponse)
        - **`accessToken`**: 엑세스 토큰
        - **`refreshToken`**: 리프레쉬 토큰

        ## 에러코드
        - **`INVALID_CREDENTIALS`**: ID와 비밀번호를 정확히 입력해 주십시오.
        - **`MEMBER_NOT_FOUND`**: 회원을 찾을 수 없습니다.
        """
  )
  ResponseEntity<LoginResponse> login(@ModelAttribute LoginRequest loginRequest);

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.15",
          author = Author.SUHSAECHAN,
          issueNumber = 10,
          description = "반환값 응답코드 201으로만 수정, 본문 삽입되는 Member 삭제"
      ),
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "회원가입 기능 구현"
      )
  })
  @Operation(
      summary = "회원가입",
      description = """
      ## 인증(JWT): **불필요**

      ## 참고사항
      - **`email`**: 이미 존재하는 이메일은 사용할 수 없습니다.
      - **`password`**: 비밀번호는 암호화되어 저장됩니다.
      - **`nickname`**: 중복된 닉네임은 사용할 수 없습니다.

      ## 요청 파라미터 (SignupRequest)
      - **`email`**: 이메일 주소 (형식 준수 필수)
      - **`password`**: 비밀번호 (최소 8자리 이상)
      - **`nickname`**: 닉네임 (중복 불가)

      ## 반환값
      - 회원가입 성공 시 HTTP 201 CREATED 상태 코드 반환 (응답 본문 없음)

      ## 에러코드
      - **`EMAIL_DUPLICATION`**: 중복된 이메일입니다.
      - **`NICKNAME_DUPLICATION`**: 중복된 닉네임입니다.
      """
  )
  ResponseEntity<SignUpResponse> signup(@ModelAttribute SignupRequest signupRequest);

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.15",
          author = Author.SUHSAECHAN,
          issueNumber = 10,
          description = "엑세스 토큰, 리프레시 토큰 갱신 (refresh token rotation) 방식으로 수정"
      ),
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "액세스 토큰 갱신 기능 구현"
      )
  })
  @Operation(
      summary = "액세스 토큰 갱신 (토큰 회전 방식)",
      description = """
      ## 인증(JWT): **불필요**

      ## 참고사항
      - **`refreshToken`**: 기존 발급된 리프레시 토큰을 사용하여, 만료 여부와 상관없이 항상 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
      
      ## 요청 파라미터 (RefreshAccessTokenByRefreshTokenRequest)
      - **`refreshToken`**: 기존 발급된 리프레시 토큰

      ## 반환값 (RefreshAccessTokenByRefreshTokenResponse)
      - **`accessToken`**: 새로운 액세스 토큰 
      - **`refreshToken`**: 갱신된 리프레시 토큰

      ## 에러코드
      - **`INVALID_REFRESH_TOKEN`**: 유효하지 않은 Refresh Token 입니다.
      - **`MEMBER_NOT_FOUND`**: 회원을 찾을 수 없습니다.
      """
  )
  ResponseEntity<RefreshAccessTokenByRefreshTokenResponse> refreshAccessTokenByRefreshToken(@ModelAttribute RefreshAccessTokenByRefreshTokenRequest request);


  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.03.06",
          author = Author.SUHSAECHAN,
          issueNumber = 30,
          description = "로그아웃 API 위치 변경, 기존 Member API 쪽 -> Auth API로 변경"
      ),
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
          
          ## 요청 파라미터 (LogoutRequest)
          - **`refreshToken`**: 로그아웃을 위한 리프레시 토큰

          ## 반환값
          - 없음 (204 No Content)
          
          ## 에러코드
          - **`INVALID_REFRESH_TOKEN`**: 유효하지 않은 Refresh Token 입니다.
          - **`REFRESH_TOKEN_NOT_FOUND`**: 존재하지 않는 Refresh Token 입니다.
          """
  )
  ResponseEntity<Void> logout(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute LogoutRequest request );

}
