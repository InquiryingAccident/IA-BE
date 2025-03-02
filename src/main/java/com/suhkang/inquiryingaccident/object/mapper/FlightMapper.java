package com.suhkang.inquiryingaccident.object.mapper;

import com.suhkang.inquiryingaccident.object.dao.Flight;
import com.suhkang.inquiryingaccident.object.dao.FlightOperator;
import com.suhkang.inquiryingaccident.object.response.FlightInfoByFlightAwareApiResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FlightMapper {
  FlightMapper INSTANCE = Mappers.getMapper(FlightMapper.class);

  // 엔티티 -> DTO 매핑
  @Mapping(source = "aircraftType.modelCode", target = "aircraftType")
  @Mapping(source = "operator", target = "operator", qualifiedByName = "flightOperatorToString")
  FlightInfoByFlightAwareApiResponse.Flight toDto(Flight entity);

  // DTO -> 엔티티 매핑
  @Mapping(target = "aircraftType", ignore = true) // AircraftType은 서비스에서 별도 처리
  @Mapping(target = "operator", ignore = true)     // Operator는 서비스에서 별도 처리
  Flight toEntity(FlightInfoByFlightAwareApiResponse.Flight dto);

  List<FlightInfoByFlightAwareApiResponse.Flight> toDtoList(List<Flight> entities);

  List<Flight> toEntityList(List<FlightInfoByFlightAwareApiResponse.Flight> dtos);

  default FlightInfoByFlightAwareApiResponse toResponse(List<Flight> entities) {
    return new FlightInfoByFlightAwareApiResponse(toDtoList(entities), null, 1);
  }

  // FlightOperator -> String 변환 메서드
  @org.mapstruct.Named("flightOperatorToString")
  default String mapFlightOperatorToString(FlightOperator operator) {
    if (operator == null) {
      return null;
    }
    // 예: ICAO 코드 또는 Name을 String으로 반환 (필요에 따라 수정)
    return operator.getIcao() != null ? operator.getIcao() : operator.getName();
  }
}