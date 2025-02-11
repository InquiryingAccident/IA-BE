package com.suhkang.inquiryingaccident.object.dao;

import com.suhkang.inquiryingaccident.object.constants.AircraftRegistrationCode;
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

  @Column(columnDefinition = "text")
  private String wikibaseId;

  private LocalDate accidentDate;

  @Column(columnDefinition = "text")
  private String aircraftType;

  @Column(columnDefinition = "text")
  private String registration;

  @Column(columnDefinition = "text")
  private String operator;

  private Integer fatalities;

  @Column(columnDefinition = "text")
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "text")
  private AircraftRegistrationCode aircraftRegistrationCode;

  @Column(columnDefinition = "text")
  private String damage;

  private Boolean hasPreliminaryReport;
}
