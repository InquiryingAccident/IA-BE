package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
  Optional<Member> findByNickname(String nickname);
  boolean existsByNickname(String nickname);
}
