package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.object.request.LoginRequest;
import com.suhkang.inquiryingaccident.object.request.RefreshAccessTokenByRefreshTokenRequest;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.object.response.LoginResponse;
import com.suhkang.inquiryingaccident.object.response.RefreshAccessTokenByRefreshTokenResponse;
import com.suhkang.inquiryingaccident.object.response.SignUpResponse;
import com.suhkang.inquiryingaccident.util.log.ApiChangeLog;
import com.suhkang.inquiryingaccident.util.log.ApiChangeLogs;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
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

      ## 반환값 (SignUpResponse)
      - **`member`**: 회원 정보 객체 (가입된 회원의 상세 정보 포함)

      ## 에러코드
      - **`MEMBER_ALREADY_EXISTS`**: 이미 존재하는 회원입니다.
      """
  )
  ResponseEntity<SignUpResponse> signup(@ModelAttribute SignupRequest signupRequest);

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "액세스 토큰 갱신 기능 구현"
      )
  })
  @Operation(
      summary = "액세스 토큰 갱신",
      description = """
      ## 인증(JWT): **불필요**

      ## 참고사항
      - **`refreshToken`**: 유효한 리프레시 토큰이 있어야 합니다.
      - **`expiredRefreshToken`**: 만료된 리프레시 토큰은 사용할 수 없으며, 재로그인이 필요합니다.

      ## 요청 파라미터 (RefreshAccessTokenByRefreshTokenRequest)
      - **`refreshToken`**: 기존 발급된 리프레시 토큰

      ## 반환값 (RefreshAccessTokenByRefreshTokenResponse)
      - **`accessToken`**: 새로운 액세스 토큰 (로그인 유지용)

      ## 에러코드
      - **`INVALID_REFRESH_TOKEN`**: 유효하지 않은 Refresh Token 입니다.
      - **`EXPIRED_REFRESH_TOKEN`**: 만료된 Refresh Token 입니다.
      - **`MEMBER_NOT_FOUND`**: 회원을 찾을 수 없습니다.
      """
  )
  ResponseEntity<RefreshAccessTokenByRefreshTokenResponse> refreshAccessTokenByRefreshToken(@ModelAttribute RefreshAccessTokenByRefreshTokenRequest request);
}
