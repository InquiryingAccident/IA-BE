package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.AccidentDBYearStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccidentDBYearStatusRepository extends JpaRepository<AccidentDBYearStatus, UUID> {
  Optional<AccidentDBYearStatus> findByYear(int year);
}
