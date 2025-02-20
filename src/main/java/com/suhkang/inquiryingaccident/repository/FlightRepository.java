package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.dao.Flight;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
  Flight findByIdent(String ident);

  List<Flight> findAllByIdent(String flightNumber);

  Flight findByRegistration(String registration);
}
