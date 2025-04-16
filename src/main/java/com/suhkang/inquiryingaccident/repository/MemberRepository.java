package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.constants.SocialPlatform;
import com.suhkang.inquiryingaccident.object.dao.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
  Optional<Member> findByEmail(String email);
  Optional<Member> findByNickname(String nickname);
  boolean existsByNickname(String nickname);
  boolean existsByEmail(String email);

  Optional<Member> findByEmailAndSocialPlatform(String email, SocialPlatform socialPlatform);

  Optional<Member> findBySocialPlatformAndSocialPlatformId(SocialPlatform socialPlatform, String socialPlatformId);
}
