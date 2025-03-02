package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AircraftRepository extends JpaRepository<Aircraft, UUID> {

  boolean existsByModelCode(String modelCode);

  Optional<Aircraft> findByModelCode(String modelCode);
}