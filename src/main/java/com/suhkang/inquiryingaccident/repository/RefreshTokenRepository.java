package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(String token);
  Optional<RefreshToken> findByMemberId(UUID memberId);
  void deleteByToken(String token);
}
