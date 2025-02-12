package com.suhkang.inquiryingaccident.global.object;

import com.suhkang.inquiryingaccident.object.constants.HashType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashRegistryRepository extends JpaRepository<HashRegistry, UUID> {
  Optional<HashRegistry> findByHashType(HashType hashType);
}
