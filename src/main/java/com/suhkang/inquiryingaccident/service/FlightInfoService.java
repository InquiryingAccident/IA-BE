package com.suhkang.inquiryingaccident.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.object.dao.Airport;
import com.suhkang.inquiryingaccident.object.dao.Flight;
import com.suhkang.inquiryingaccident.object.request.FlightInfoByFlightAwareApiRequest;
import com.suhkang.inquiryingaccident.object.response.FlightInfoByFlightAwareApiResponse;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import com.suhkang.inquiryingaccident.repository.AirportRepository;
import com.suhkang.inquiryingaccident.repository.FlightRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightInfoService {

  private final OkHttpClient okHttpClient;
  private final FlightRepository flightRepository;
  private final AirportRepository airportRepository;
  private final AircraftTypeRepository aircraftTypeRepository;
  private final ObjectMapper objectMapper;

  @Value("${flightaware.api-key}")
  private String flightAwareApiKey;

  @Value("${flightaware.base-url}")
  private String flightAwareBaseUrl;

  /**
   * 항공편 번호에 대한 정보를 FlightAware API를 통해 조회하거나,
   * DB에 해당 항공편(ident 또는 registration이 일치)이 이미 존재하면 바로 반환합니다.
   */
  public FlightInfoByFlightAwareApiResponse getFlightInfoByFlightAwareApi(
      FlightInfoByFlightAwareApiRequest flightInfoByFlightAwareApiRequest) {
    String flightNumber = flightInfoByFlightAwareApiRequest.getFlightNumber();
    log.info("요청 받은 항공편 번호: {}", flightNumber);

    // DB에서 ident 기준으로 항공편 목록 검색
    List<Flight> existingFlights = flightRepository.findAllByIdent(flightNumber);
    if (existingFlights != null && !existingFlights.isEmpty()) {
      log.info("DB에서 {} 건의 항공편을 찾았습니다. ident: {}", existingFlights.size(), flightNumber);
      return convertEntityListToDto(existingFlights);
    }

    // DB에 없으면 FlightAware API 호출
    String url = flightAwareBaseUrl + "/flights/" + flightNumber;
    Request request = new Request.Builder()
        .url(url)
        .header("x-apikey", flightAwareApiKey)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
        .header("Accept", "application/json")
        .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        log.error("FlightAware API 호출 실패: {} - 응답 코드: {}", url, response.code());
        return new FlightInfoByFlightAwareApiResponse();
      }
      String responseBody = response.body().string();
      log.info("API 응답 본문: {}", responseBody);

      FlightInfoByFlightAwareApiResponse responseDTO =
          objectMapper.readValue(responseBody, FlightInfoByFlightAwareApiResponse.class);

      // API 응답의 모든 항공편을 순회하며 DB에 저장 (중복 등록번호 및 null 검사)
      List<Flight> flightsToSave = new ArrayList<>();
      if (responseDTO.getFlights() != null) {
        log.info("API 응답으로부터 {} 건의 항공편 정보를 처리합니다.", responseDTO.getFlights().size());
        for (FlightInfoByFlightAwareApiResponse.Flight flightDto : responseDTO.getFlights()) {
          // 등록번호(registration)는 필수이므로 null이면 저장하지 않음
          if (flightDto.getRegistration() == null) {
            log.warn("등록번호가 null인 항공편은 저장하지 않습니다. ident={}", flightDto.getIdent());
            continue;
          }
          // 이미 등록된 항공편이면 저장하지 않고 로그 출력
          if (flightRepository.findByRegistration(flightDto.getRegistration()) != null) {
            log.info("등록번호 {} 인 항공편이 이미 존재합니다. 저장을 건너뜁니다.", flightDto.getRegistration());
            continue;
          }
          Flight flight = convertDtoToEntity(flightDto);
          if (flight != null) {
            flightsToSave.add(flight);
          }
        }
      }
      if (!flightsToSave.isEmpty()) {
        log.info("새로운 항공편 {} 건을 DB에 저장합니다.", flightsToSave.size());
        flightRepository.saveAll(flightsToSave);
      }
      return responseDTO;
    } catch (IOException ex) {
      log.error("FlightAware API 호출 중 오류 발생: {}", ex.getMessage(), ex);
      return new FlightInfoByFlightAwareApiResponse();
    }
  }

  // 엔티티 목록 -> DTO 변환 (전체 항공편 목록 매핑)
  private FlightInfoByFlightAwareApiResponse convertEntityListToDto(List<Flight> flights) {
    List<FlightInfoByFlightAwareApiResponse.Flight> flightDtoList = new ArrayList<>();
    for (Flight flight : flights) {
      FlightInfoByFlightAwareApiResponse.Flight flightDto = new FlightInfoByFlightAwareApiResponse.Flight();
      flightDto.setIdent(flight.getIdent());
      flightDto.setIdentIcao(flight.getIdentIcao());
      flightDto.setIdentIata(flight.getIdentIata());
      flightDto.setActualRunwayOff(flight.getActualRunwayOff());
      flightDto.setActualRunwayOn(flight.getActualRunwayOn());
      flightDto.setFaFlightId(flight.getFaFlightId());
      flightDto.setOperator(flight.getOperator() != null ? flight.getOperator().toString() : null);
      flightDto.setFlightNumber(flight.getFlightNumber());
      flightDto.setRegistration(flight.getRegistration());
      flightDto.setAtcIdent(flight.getAtcIdent());
      flightDto.setInboundFaFlightId(flight.getInboundFaFlightId());
      flightDto.setCodeshares(flight.getCodeshares());
      flightDto.setCodesharesIata(flight.getCodesharesIata());
      flightDto.setBlocked(flight.getBlocked());
      flightDto.setDiverted(flight.getDiverted());
      flightDto.setCancelled(flight.getCancelled());
      flightDto.setPositionOnly(flight.getPositionOnly());

      // origin 매핑
      if (flight.getOrigin() != null) {
        FlightInfoByFlightAwareApiResponse.Airport originDto = new FlightInfoByFlightAwareApiResponse.Airport();
        originDto.setCode(flight.getOrigin().getCode());
        originDto.setCodeIcao(flight.getOrigin().getCodeIcao());
        originDto.setCodeIata(flight.getOrigin().getCodeIata());
        originDto.setCodeLid(flight.getOrigin().getCodeLid());
        originDto.setTimezone(flight.getOrigin().getTimezone());
        originDto.setName(flight.getOrigin().getName());
        originDto.setCity(flight.getOrigin().getCity());
        originDto.setAirportInfoUrl(flight.getOrigin().getAirportInfoUrl());
        flightDto.setOrigin(originDto);
      }
      // destination 매핑
      if (flight.getDestination() != null) {
        FlightInfoByFlightAwareApiResponse.Airport destDto = new FlightInfoByFlightAwareApiResponse.Airport();
        destDto.setCode(flight.getDestination().getCode());
        destDto.setCodeIcao(flight.getDestination().getCodeIcao());
        destDto.setCodeIata(flight.getDestination().getCodeIata());
        destDto.setCodeLid(flight.getDestination().getCodeLid());
        destDto.setTimezone(flight.getDestination().getTimezone());
        destDto.setName(flight.getDestination().getName());
        destDto.setCity(flight.getDestination().getCity());
        destDto.setAirportInfoUrl(flight.getDestination().getAirportInfoUrl());
        flightDto.setDestination(destDto);
      }
      flightDto.setDepartureDelay(flight.getDepartureDelay());
      flightDto.setArrivalDelay(flight.getArrivalDelay());
      flightDto.setFiledEte(flight.getFiledEte());
      flightDto.setForesightPredictionsAvailable(flight.getForesightPredictionsAvailable());
      flightDto.setScheduledOut(flight.getScheduledOut());
      flightDto.setEstimatedOut(flight.getEstimatedOut());
      flightDto.setActualOut(flight.getActualOut());
      flightDto.setScheduledOff(flight.getScheduledOff());
      flightDto.setEstimatedOff(flight.getEstimatedOff());
      flightDto.setActualOff(flight.getActualOff());
      flightDto.setScheduledOn(flight.getScheduledOn());
      flightDto.setEstimatedOn(flight.getEstimatedOn());
      flightDto.setActualOn(flight.getActualOn());
      flightDto.setScheduledIn(flight.getScheduledIn());
      flightDto.setEstimatedIn(flight.getEstimatedIn());
      flightDto.setActualIn(flight.getActualIn());
      flightDto.setProgressPercent(flight.getProgressPercent());
      flightDto.setStatus(flight.getStatus());
      // aircraftType 매핑: modelCode 사용
      flightDto.setAircraftType(flight.getAircraftType() != null ? flight.getAircraftType().getModelCode() : null);
      flightDto.setRouteDistance(flight.getRouteDistance());
      flightDto.setFiledAirspeed(flight.getFiledAirspeed());
      flightDto.setFiledAltitude(flight.getFiledAltitude());
      flightDto.setRoute(flight.getRoute());
      flightDto.setBaggageClaim(flight.getBaggageClaim());
      flightDto.setSeatsCabinBusiness(flight.getSeatsCabinBusiness());
      flightDto.setSeatsCabinCoach(flight.getSeatsCabinCoach());
      flightDto.setSeatsCabinFirst(flight.getSeatsCabinFirst());
      flightDto.setGateOrigin(flight.getGateOrigin());
      flightDto.setGateDestination(flight.getGateDestination());
      flightDto.setTerminalOrigin(flight.getTerminalOrigin());
      flightDto.setTerminalDestination(flight.getTerminalDestination());
      flightDto.setType(flight.getType());
      flightDtoList.add(flightDto);
    }
    FlightInfoByFlightAwareApiResponse responseDTO = new FlightInfoByFlightAwareApiResponse();
    responseDTO.setFlights(flightDtoList);
    responseDTO.setLinks(null);
    responseDTO.setNumPages(1);
    return responseDTO;
  }

  // DTO -> DB 엔티티 변환 (모든 필드를 매핑)
  private Flight convertDtoToEntity(FlightInfoByFlightAwareApiResponse.Flight flightDto) {
    if (flightDto.getRegistration() == null) {
      log.warn("등록번호가 null인 항공편은 변환하지 않습니다. ident={}", flightDto.getIdent());
      return null;
    }
    log.info("등록번호 '{}'인 항공편 DTO를 엔티티로 변환합니다.", flightDto.getRegistration());
    Flight.FlightBuilder builder = Flight.builder();
    builder.ident(flightDto.getIdent())
        .identIcao(flightDto.getIdentIcao())
        .identIata(flightDto.getIdentIata())
        .actualRunwayOff(flightDto.getActualRunwayOff())
        .actualRunwayOn(flightDto.getActualRunwayOn())
        .faFlightId(flightDto.getFaFlightId())
        .flightNumber(flightDto.getFlightNumber())
        .registration(flightDto.getRegistration())
        .atcIdent(flightDto.getAtcIdent())
        .inboundFaFlightId(flightDto.getInboundFaFlightId())
        .codeshares(flightDto.getCodeshares())
        .codesharesIata(flightDto.getCodesharesIata())
        .blocked(flightDto.getBlocked())
        .diverted(flightDto.getDiverted())
        .cancelled(flightDto.getCancelled())
        .positionOnly(flightDto.getPositionOnly())
        .departureDelay(flightDto.getDepartureDelay())
        .arrivalDelay(flightDto.getArrivalDelay())
        .filedEte(flightDto.getFiledEte())
        .foresightPredictionsAvailable(flightDto.getForesightPredictionsAvailable())
        .scheduledOut(flightDto.getScheduledOut())
        .estimatedOut(flightDto.getEstimatedOut())
        .actualOut(flightDto.getActualOut())
        .scheduledOff(flightDto.getScheduledOff())
        .estimatedOff(flightDto.getEstimatedOff())
        .actualOff(flightDto.getActualOff())
        .scheduledOn(flightDto.getScheduledOn())
        .estimatedOn(flightDto.getEstimatedOn())
        .actualOn(flightDto.getActualOn())
        .scheduledIn(flightDto.getScheduledIn())
        .estimatedIn(flightDto.getEstimatedIn())
        .actualIn(flightDto.getActualIn())
        .progressPercent(flightDto.getProgressPercent())
        .status(flightDto.getStatus())
        .routeDistance(flightDto.getRouteDistance())
        .filedAirspeed(flightDto.getFiledAirspeed())
        .filedAltitude(flightDto.getFiledAltitude())
        .route(flightDto.getRoute())
        .baggageClaim(flightDto.getBaggageClaim())
        .seatsCabinBusiness(flightDto.getSeatsCabinBusiness())
        .seatsCabinCoach(flightDto.getSeatsCabinCoach())
        .seatsCabinFirst(flightDto.getSeatsCabinFirst())
        .gateOrigin(flightDto.getGateOrigin())
        .gateDestination(flightDto.getGateDestination())
        .terminalOrigin(flightDto.getTerminalOrigin())
        .terminalDestination(flightDto.getTerminalDestination())
        .type(flightDto.getType());

    // Origin 매핑: 공항 코드가 동일하면 재사용
    if (flightDto.getOrigin() != null) {
      log.info("Origin 공항 매핑 중: 코드 = {}", flightDto.getOrigin().getCode());
      Airport origin = airportRepository.findByCode(flightDto.getOrigin().getCode());
      if (origin == null) {
        log.info("Origin 공항이 DB에 존재하지 않으므로 새로 생성합니다. 코드 = {}", flightDto.getOrigin().getCode());
        origin = Airport.builder()
            .code(flightDto.getOrigin().getCode())
            .codeIcao(flightDto.getOrigin().getCodeIcao())
            .codeIata(flightDto.getOrigin().getCodeIata())
            .codeLid(flightDto.getOrigin().getCodeLid())
            .timezone(flightDto.getOrigin().getTimezone())
            .name(flightDto.getOrigin().getName())
            .city(flightDto.getOrigin().getCity())
            .airportInfoUrl(flightDto.getOrigin().getAirportInfoUrl())
            .build();
      } else {
        log.info("Origin 공항을 재사용합니다. 코드 = {}", flightDto.getOrigin().getCode());
      }
      builder.origin(origin);
    }

    // Destination 매핑: 공항 코드가 동일하면 재사용
    if (flightDto.getDestination() != null) {
      log.info("Destination 공항 매핑 중: 코드 = {}", flightDto.getDestination().getCode());
      Airport destination = airportRepository.findByCode(flightDto.getDestination().getCode());
      if (destination == null) {
        log.info("Destination 공항이 DB에 존재하지 않으므로 새로 생성합니다. 코드 = {}", flightDto.getDestination().getCode());
        destination = Airport.builder()
            .code(flightDto.getDestination().getCode())
            .codeIcao(flightDto.getDestination().getCodeIcao())
            .codeIata(flightDto.getDestination().getCodeIata())
            .codeLid(flightDto.getDestination().getCodeLid())
            .timezone(flightDto.getDestination().getTimezone())
            .name(flightDto.getDestination().getName())
            .city(flightDto.getDestination().getCity())
            .airportInfoUrl(flightDto.getDestination().getAirportInfoUrl())
            .build();
      } else {
        log.info("Destination 공항을 재사용합니다. 코드 = {}", flightDto.getDestination().getCode());
      }
      builder.destination(destination);
    }

    // AircraftType 매핑: API에서 받은 문자열(예:"B773")를 기준으로 DB에서 조회
    if (flightDto.getAircraftType() != null) {
      log.info("AircraftType 매핑 중: 모델 코드 = {}", flightDto.getAircraftType());
      Optional<AircraftType> aircraftTypeOpt = aircraftTypeRepository.findByModelCode(flightDto.getAircraftType());
      if (aircraftTypeOpt.isPresent()) {
        log.info("AircraftType 찾음: {}", aircraftTypeOpt.get());
        builder.aircraftType(aircraftTypeOpt.get());
      } else {
        log.warn("AircraftType을 찾지 못했습니다: 모델 코드 = {}", flightDto.getAircraftType());
        builder.aircraftType(null);
      }
    } else {
      log.info("AircraftType 정보가 제공되지 않았습니다.");
    }

    // Operator 매핑: 현재 API에서 상세정보가 없으므로 null 처리 (향후 구현)
    log.info("Operator 매핑은 아직 구현되지 않았습니다.");
    builder.operator(null);

    return builder.build();
  }
}
