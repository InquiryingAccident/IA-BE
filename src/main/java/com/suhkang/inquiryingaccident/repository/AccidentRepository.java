package com.suhkang.inquiryingaccident.repository;

import com.suhkang.inquiryingaccident.object.constants.CommonStatus;
import com.suhkang.inquiryingaccident.object.dao.Accident;
import com.suhkang.inquiryingaccident.object.constants.AircraftRegistrationCode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccidentRepository extends JpaRepository<Accident, UUID> {

  @Query("SELECT a FROM Accident a " +
      "WHERE (:wikibaseId = '' OR a.wikibaseId LIKE CONCAT('%', :wikibaseId, '%')) " +
      "  AND (:accidentDate IS NULL OR a.accidentDate = :accidentDate) " +
      "  AND (:aircraftType = '' OR a.aircraftType LIKE CONCAT('%', :aircraftType, '%')) " +
      "  AND (:registration = '' OR a.registration LIKE CONCAT('%', :registration, '%')) " +
      "  AND (:operator = '' OR a.operator LIKE CONCAT('%', :operator, '%')) " +
      "  AND (:fatalities IS NULL OR a.fatalities = :fatalities) " +
      "  AND (:location = '' OR a.location LIKE CONCAT('%', :location, '%')) " +
      "  AND (:arc IS NULL OR a.aircraftRegistrationCode = :arc) " +
      "  AND (:damage = '' OR a.damage LIKE CONCAT('%', :damage, '%')) " +
      "  AND (:hasPreliminaryReport IS NULL OR a.hasPreliminaryReport = :hasPreliminaryReport)")
  Page<Accident> findByDynamicQuery(
      @Param("wikibaseId") String wikibaseId,
      @Param("accidentDate") LocalDate accidentDate,
      @Param("aircraftType") String aircraftType,
      @Param("registration") String registration,
      @Param("operator") String operator,
      @Param("fatalities") Integer fatalities,
      @Param("location") String location,
      @Param("arc") AircraftRegistrationCode arc,
      @Param("damage") String damage,
      @Param("hasPreliminaryReport") Boolean hasPreliminaryReport,
      Pageable pageable);


  @Query("SELECT count(a) FROM Accident a WHERE extract(year from a.accidentDate) = :year")
  int countByAccidentYear(@Param("year") int year);

  @Query("SELECT count(a) FROM Accident a WHERE extract(year from a.accidentDate) = :year AND a.commonStatus = :commonStatus")
  int countByAccidentYearAndCommonStatus(@Param("year") int year,
      @Param("commonStatus") CommonStatus commonStatus);

  @Query("SELECT a FROM Accident a WHERE extract(year from a.accidentDate) = :year")
  List<Accident> findByAccidentYear(@Param("year") int year);
}
