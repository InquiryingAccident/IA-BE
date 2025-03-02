package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.global.exception.CustomException;
import com.suhkang.inquiryingaccident.global.exception.ErrorCode;
import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import com.suhkang.inquiryingaccident.object.dao.Flight;
import com.suhkang.inquiryingaccident.object.mapper.FlightMapper;
import com.suhkang.inquiryingaccident.object.request.AircraftInfoByModelCodeRequest;
import com.suhkang.inquiryingaccident.object.request.FlightInfoByFlightAwareApiRequest;
import com.suhkang.inquiryingaccident.object.response.FlightInfoByFlightAwareApiResponse;
import com.suhkang.inquiryingaccident.repository.AircraftRepository;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import com.suhkang.inquiryingaccident.repository.FlightRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightInfoService {

  private final FlightRepository flightRepository;
  private final AircraftTypeRepository aircraftTypeRepository;
  private final FlightAwareService flightAwareService;
  private final AircraftRepository aircraftRepository;

  public FlightInfoByFlightAwareApiResponse getFlightInfoByFlightAwareApi(FlightInfoByFlightAwareApiRequest request) {
    String flightNumber = request.getFlightNumber();
    log.info("요청 받은 항공편 번호: {}", flightNumber);

    List<Flight> existingFlights = flightRepository.findAllByIdent(flightNumber);
    if (!existingFlights.isEmpty()) {
      log.info("DB에서 {} 건의 항공편을 찾았습니다. ident: {}", existingFlights.size(), flightNumber);
      return FlightMapper.INSTANCE.toResponse(existingFlights);
    }

    FlightInfoByFlightAwareApiResponse response = flightAwareService.fetchFlightInfo(request);
    List<Flight> flightsToSave = response.getFlights().stream()
        .filter(dto -> dto.getRegistration() != null)
        .filter(dto -> flightRepository.findByRegistration(dto.getRegistration()) == null)
        .map(dto -> {
          Flight entity = FlightMapper.INSTANCE.toEntity(dto);
          if (dto.getAircraftType() != null) {
            aircraftTypeRepository.findByModelCode(dto.getAircraftType())
                .ifPresent(entity::setAircraftType);
          }
          return entity;
        })
        .collect(Collectors.toList());

    if (!flightsToSave.isEmpty()) {
      log.info("새로운 항공편 {} 건 저장", flightsToSave.size());
      flightRepository.saveAll(flightsToSave);
    }
    return response;
  }

  public Aircraft getAircraftTypeInfo(AircraftInfoByModelCodeRequest request) {
    String modelCode = request.getModelCode().toUpperCase();
    log.info("항공기 타입 조회 요청: modelCode = {}", modelCode);

    Optional<com.suhkang.inquiryingaccident.object.dao.Aircraft> aircraftOpt = aircraftRepository.findByModelCode(modelCode);

    if (aircraftOpt.isPresent()) {
      com.suhkang.inquiryingaccident.object.dao.Aircraft aircraft = aircraftOpt.get();
      log.info("항공기 조회 성공: {}", aircraft);
      return aircraft;
    } else {
      log.warn("항공기 타입을 찾지 못함: modelCode = {}", modelCode);
      throw new CustomException(ErrorCode.AIRCRAFT_NOT_FOUND);
    }
  }
}