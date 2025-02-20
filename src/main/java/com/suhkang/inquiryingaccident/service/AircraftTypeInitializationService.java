package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AircraftTypeInitializationService {

  private final AircraftTypeRepository aircraftTypeRepository;
  private final AircraftTypeScrapingService aircraftTypeScrapingService;

  // 상업용/기업용 두 URL
  private static final String COMMERCIAL_JET_INFO_URL = "https://asn.flightsafety.org/asndb/types/CJ";
  private static final String CORPORATE_JET_INFO_URL  = "https://asn.flightsafety.org/asndb/types/BJ";

  /**
   * DB 초기화 시 두 URL에서 스크래핑한 항공기 정보를
   * 파싱할 때마다 DB에 저장(신규 등록 또는 업데이트)합니다.
   */
  public void initDatabase() {
    // 1. 상업용 항공기(Commercial) 스크래핑 및 저장
    List<AircraftType> commercialTypes =
        aircraftTypeScrapingService.scrapeAircraftTypes(COMMERCIAL_JET_INFO_URL, AircraftUsageType.COMMERCIAL);
    commercialTypes.forEach(type -> {
      aircraftTypeRepository.findByModelCode(type.getModelCode())
          .ifPresentOrElse(existing -> {
        // 기존 데이터 업데이트
        existing.setModelName(type.getModelName());
        existing.setManufacturer(type.getManufacturer());
        existing.setFirstFlightYear(type.getFirstFlightYear());
        existing.setDescription(type.getDescription());
        existing.setAircraftUsageType(type.getAircraftUsageType());
        existing.setAsnAccidentUrl(type.getAsnAccidentUrl());
        aircraftTypeRepository.save(existing);
        log.info("업데이트: {}", existing);
      }, () -> {
        // 신규 등록
        aircraftTypeRepository.save(type);
        log.info("신규 등록: {}", type);
      });
    });

    // 2. 비즈니스/기업용 항공기(Corporate) 스크래핑 및 저장
    List<AircraftType> corporateTypes =
        aircraftTypeScrapingService.scrapeAircraftTypes(CORPORATE_JET_INFO_URL, AircraftUsageType.CORPORATE);
    corporateTypes.forEach(type -> {
      aircraftTypeRepository.findByModelCode(type.getModelCode()).ifPresentOrElse(existing -> {
        existing.setModelName(type.getModelName());
        existing.setManufacturer(type.getManufacturer());
        existing.setFirstFlightYear(type.getFirstFlightYear());
        existing.setDescription(type.getDescription());
        existing.setAircraftUsageType(type.getAircraftUsageType());
        existing.setAsnAccidentUrl(type.getAsnAccidentUrl());
        aircraftTypeRepository.save(existing);
        log.info("업데이트: {}", existing);
      }, () -> {
        aircraftTypeRepository.save(type);
        log.info("신규 등록: {}", type);
      });
    });
  }
}
