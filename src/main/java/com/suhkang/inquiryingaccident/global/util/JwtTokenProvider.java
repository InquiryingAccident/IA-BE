package com.suhkang.inquiryingaccident.global.util;

import com.suhkang.inquiryingaccident.object.constants.JwtTokenType;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.global.exception.CustomException;
import com.suhkang.inquiryingaccident.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  // 2025.02.09 : jwtToken 만료시간 ENUM Handling 으로 변경
//  @Value("${jwt.access-exp-time}")
//  private long accessTokenValidityInMilliseconds;
//
//  // refresh 토큰 만료 시간 (밀리초)
//  @Value("${jwt.refresh-exp-time}")
//  private long refreshTokenValidityInMilliseconds;

  @Value("${jwt.issuer}")
  private String issuer;

  private Key getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // token 생성 (memberIdStr) 저장 : ACCESS, REFRESH 따로
  public String generateToken(Authentication authentication, JwtTokenType jwtTokenType) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String memberIdStr = userDetails.getMember().getMemberId().toString();
    Date now = new Date();

    // 2025.02.09 : jwtToken 만료시간 ENUM Handling 으로 변경
//    long duration = (jwtTokenType == JwtTokenType.ACCESS)
//        ? accessTokenValidityInMilliseconds
//        : refreshTokenValidityInMilliseconds;

    Date expiryDate = new Date(now.getTime() + jwtTokenType.getDurationMilliseconds());

    return Jwts.builder()
        .setSubject(memberIdStr)
        .setIssuer(issuer)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  // token -> memberId 반환
  public UUID getMemberIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    try {
      return UUID.fromString(claims.getSubject());
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage(), e);
      throw new CustomException(ErrorCode.INVALID_MEMBER_ID_FORMAT);
    }
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      // 토큰 검증 실패 시
      log.error(e.getMessage(), e);
    }
    return false;
  }
}
