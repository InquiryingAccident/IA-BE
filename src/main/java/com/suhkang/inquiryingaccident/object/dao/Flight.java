package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Flight extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID flightId;

  private String ident;

  private String identIcao;

  private String identIata;

  private String actualRunwayOff;

  private String actualRunwayOn;

  private String faFlightId;

  private String flightNumber;

  private String registration;

  private String atcIdent;

  private String inboundFaFlightId;

  @ElementCollection
  private List<String> codeshares;

  @ElementCollection
  private List<String> codesharesIata;

  private Boolean blocked;
  private Boolean diverted;
  private Boolean cancelled;
  private Boolean positionOnly;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Airport origin;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Airport destination;

  private Integer departureDelay;

  private Integer arrivalDelay;

  private Integer filedEte;

  private Boolean foresightPredictionsAvailable;

  private String scheduledOut;

  private String estimatedOut;

  private String actualOut;

  private String scheduledOff;

  private String estimatedOff;

  private String actualOff;

  private String scheduledOn;

  private String estimatedOn;

  private String actualOn;

  private String scheduledIn;

  private String estimatedIn;

  private String actualIn;

  private Integer progressPercent;

  private String status;

  private Integer routeDistance;

  private Integer filedAirspeed;

  private Integer filedAltitude;

  private String route;

  private String baggageClaim;

  private String seatsCabinBusiness;

  private String seatsCabinCoach;

  private String seatsCabinFirst;

  private String gateOrigin;

  private String gateDestination;

  private String terminalOrigin;

  private String terminalDestination;

  private String type;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private AircraftType aircraftType;

  @Embedded
  private FlightOperator operator;
}
