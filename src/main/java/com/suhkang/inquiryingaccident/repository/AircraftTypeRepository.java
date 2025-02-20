package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftTypeRepository extends JpaRepository<AircraftType, UUID> {

  Optional<AircraftType> findByModelCode(String modelCode);
}
