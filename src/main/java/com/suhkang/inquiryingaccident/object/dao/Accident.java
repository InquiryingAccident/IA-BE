package com.suhkang.inquiryingaccident.object.dao;

import com.suhkang.inquiryingaccident.object.constants.AircraftRegistrationCode;
import com.suhkang.inquiryingaccident.object.constants.CommonStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Accident {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID accidentId;

  private String wikibaseId;

  private LocalDate accidentDate;

  private String aircraftType;

  private String registration;

  private String operator;

  private Integer fatalities;

  private String location;

  @Enumerated(EnumType.STRING)
  private AircraftRegistrationCode aircraftRegistrationCode;

  private String damage;

  private Boolean hasPreliminaryReport;

  private String errorMessage;

  @Enumerated(EnumType.STRING)
  private CommonStatus commonStatus;
}
