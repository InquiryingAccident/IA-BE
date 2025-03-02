package com.suhkang.inquiryingaccident.object.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightInfoByFlightAwareApiResponse {

  @JsonProperty("flights")
  private List<Flight> flights;

  @JsonProperty("links")
  private Object links;

  @JsonProperty("num_pages")
  private int numPages;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Flight {
    private String ident;

    @JsonProperty("ident_icao")
    private String identIcao;

    @JsonProperty("ident_iata")
    private String identIata;

    @JsonProperty("actual_runway_off")
    private String actualRunwayOff;

    @JsonProperty("actual_runway_on")
    private String actualRunwayOn;

    @JsonProperty("fa_flight_id")
    private String faFlightId;

    private String operator;

    @JsonProperty("operator_icao")
    private String operatorIcao;

    @JsonProperty("operator_iata")
    private String operatorIata;

    @JsonProperty("flight_number")
    private String flightNumber;

    private String registration;

    @JsonProperty("atc_ident")
    private String atcIdent;

    @JsonProperty("inbound_fa_flight_id")
    private String inboundFaFlightId;

    private List<String> codeshares;

    @JsonProperty("codeshares_iata")
    private List<String> codesharesIata;

    private Boolean blocked;
    private Boolean diverted;
    private Boolean cancelled;

    @JsonProperty("position_only")
    private Boolean positionOnly;

    private Airport origin;
    private Airport destination;

    @JsonProperty("departure_delay")
    private Integer departureDelay;

    @JsonProperty("arrival_delay")
    private Integer arrivalDelay;

    @JsonProperty("filed_ete")
    private Integer filedEte;

    @JsonProperty("foresight_predictions_available")
    private Boolean foresightPredictionsAvailable;

    @JsonProperty("scheduled_out")
    private String scheduledOut;

    @JsonProperty("estimated_out")
    private String estimatedOut;

    @JsonProperty("actual_out")
    private String actualOut;

    @JsonProperty("scheduled_off")
    private String scheduledOff;

    @JsonProperty("estimated_off")
    private String estimatedOff;

    @JsonProperty("actual_off")
    private String actualOff;

    @JsonProperty("scheduled_on")
    private String scheduledOn;

    @JsonProperty("estimated_on")
    private String estimatedOn;

    @JsonProperty("actual_on")
    private String actualOn;

    @JsonProperty("scheduled_in")
    private String scheduledIn;

    @JsonProperty("estimated_in")
    private String estimatedIn;

    @JsonProperty("actual_in")
    private String actualIn;

    @JsonProperty("progress_percent")
    private Integer progressPercent;

    private String status;

    @JsonProperty("aircraft_type")
    private String aircraftType;

    @JsonProperty("route_distance")
    private Integer routeDistance;

    @JsonProperty("filed_airspeed")
    private Integer filedAirspeed;

    @JsonProperty("filed_altitude")
    private Integer filedAltitude;

    private String route;

    @JsonProperty("baggage_claim")
    private String baggageClaim;

    @JsonProperty("seats_cabin_business")
    private String seatsCabinBusiness;

    @JsonProperty("seats_cabin_coach")
    private String seatsCabinCoach;

    @JsonProperty("seats_cabin_first")
    private String seatsCabinFirst;

    @JsonProperty("gate_origin")
    private String gateOrigin;

    @JsonProperty("gate_destination")
    private String gateDestination;

    @JsonProperty("terminal_origin")
    private String terminalOrigin;

    @JsonProperty("terminal_destination")
    private String terminalDestination;

    private String type;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Airport {
    private String code;

    @JsonProperty("code_icao")
    private String codeIcao;

    @JsonProperty("code_iata")
    private String codeIata;

    @JsonProperty("code_lid")
    private String codeLid;

    private String timezone;
    private String name;
    private String city;

    @JsonProperty("airport_info_url")
    private String airportInfoUrl;
  }
}