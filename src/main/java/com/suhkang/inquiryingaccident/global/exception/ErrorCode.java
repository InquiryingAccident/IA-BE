package com.suhkang.inquiryingaccident.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // Common
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),

  // Member

  MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다"),

  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),

  EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "중복된 이메일 입니다"),

  NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "중복된 닉네임 입니다"),

  INVALID_MEMBER_ID_FORMAT(HttpStatus.BAD_REQUEST, "회원ID가 UUID 포맷이 아닙니다"),

  // UTILS

  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

  FILE_COPY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 복사에 실패했습니다."),

  // AUTHENTICATION

  REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh Token 을 찾을 수 없습니다"),

  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지않은 Refresh Token 입니다"),

  NOT_EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료되지 않은 토큰이 전달되었습니다. 재발급 요청은 만료된 토큰만 허용됩니다."),

  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "ID와 비밀번호를 정확히 입력해 주십시오."),

  // AIRCRAFT

  AIRCRAFT_NOT_FOUND(HttpStatus.NOT_FOUND, "항공기 정보를 찾을 수 없습니다"),

  // AIRCRAFT TYPE

  AIRCRAFT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "항공기 기종을 찾을 수 없습니다");

  private final HttpStatus httpStatus;
  private final String message;

  ErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }
}
