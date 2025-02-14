package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID refreshTokenId;

  // 리프레시 토큰 값 (유일)
  @Column(nullable = false, unique = true)
  private String token;

  // Member 엔티티의 memberId (UUID)
  @Column(nullable = false)
  private UUID memberId;

  // 회원 이메일
  @Column( nullable = false)
  private String memberEmail;

  // 리프레시 토큰 만료 시간
  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  public static boolean isExpired(RefreshToken refreshToken) {
    return refreshToken.getExpiryDate().isBefore(Instant.now());
  }
}
