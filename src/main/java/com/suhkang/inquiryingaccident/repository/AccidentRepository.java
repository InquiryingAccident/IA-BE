package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.Accident;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccidentRepository extends JpaRepository<Accident, UUID> {

}
