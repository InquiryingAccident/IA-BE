package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.AircraftTypeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AircraftTypeStatusRepository extends JpaRepository<AircraftTypeStatus, UUID> {
  Optional<AircraftTypeStatus> findTopByOrderByLastUpdatedDesc(); // 가장 최근 상태 조회
}