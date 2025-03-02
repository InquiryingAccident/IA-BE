package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AircraftRepository extends JpaRepository<Aircraft, UUID> {

  boolean existsByModelCode(String modelCode);

  Optional<Aircraft> findByModelCode(String modelCode);

  @Query("SELECT DISTINCT a.modelCode FROM Aircraft a")
  List<String> findDistinctModelCodes();
}