package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.object.dao.AircraftTypeStatus;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import com.suhkang.inquiryingaccident.repository.AircraftTypeStatusRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AircraftTypeInitializationService {

  private final AircraftTypeRepository aircraftTypeRepository;
  private final AircraftTypeStatusRepository aircraftTypeStatusRepository;
  private final AircraftTypeScrapingService aircraftTypeScrapingService;

  private static final String COMMERCIAL_JET_INFO_URL = "https://asn.flightsafety.org/asndb/types/CJ";
  private static final String CORPORATE_JET_INFO_URL = "https://asn.flightsafety.org/asndb/types/BJ";

  public void initDatabase() {
    // 1. 현재 DB 상태 확인
    AircraftTypeStatus latestStatus = aircraftTypeStatusRepository.findTopByOrderByLastUpdatedDesc()
        .orElse(null);

    // 2. 웹에서 AircraftType 데이터 가져오기
    List<AircraftType> commercialTypes = aircraftTypeScrapingService.scrapeAircraftTypes(
        COMMERCIAL_JET_INFO_URL, AircraftUsageType.COMMERCIAL);
    List<AircraftType> corporateTypes = aircraftTypeScrapingService.scrapeAircraftTypes(
        CORPORATE_JET_INFO_URL, AircraftUsageType.CORPORATE);
    int totalWebRecords = commercialTypes.size() + corporateTypes.size();

    // 3. DB에 저장된 AircraftType 개수 확인
    long dbRecordCount = aircraftTypeRepository.count();

    // 4. 변경 여부 판단: 웹 데이터 개수와 DB 개수가 다르거나, 상태가 없거나 완료되지 않은 경우
    boolean needsReparsing = latestStatus == null || !latestStatus.isComplete() || dbRecordCount != totalWebRecords;

    if (!needsReparsing) {
      log.info("AircraftType 데이터가 최신 상태입니다. (웹: {}, DB: {}). 파싱 건너뜀", totalWebRecords, dbRecordCount);
      return;
    }

    log.info("AircraftType 데이터 변경 감지 (웹: {}, DB: {}). 전체 파싱 시작", totalWebRecords, dbRecordCount);

    // 5. 기존 데이터 삭제 (선택 사항, 필요에 따라 유지 가능)
    aircraftTypeRepository.deleteAll();

    // 6. 상업용 항공기 저장
    int successCount = 0;
    int failureCount = 0;
    for (AircraftType type : commercialTypes) {
      try {
        aircraftTypeRepository.save(type);
        successCount++;
        log.info("상업용 AircraftType 신규 등록: {}", type);
      } catch (Exception e) {
        failureCount++;
        log.error("상업용 AircraftType 저장 실패: {}", type, e);
      }
    }

    // 7. 기업용 항공기 저장
    for (AircraftType type : corporateTypes) {
      try {
        aircraftTypeRepository.save(type);
        successCount++;
        log.info("기업용 AircraftType 신규 등록: {}", type);
      } catch (Exception e) {
        failureCount++;
        log.error("기업용 AircraftType 저장 실패: {}", type, e);
      }
    }

    // 8. 상태 업데이트
    AircraftTypeStatus newStatus = AircraftTypeStatus.builder()
        .totalRecords(totalWebRecords)
        .successCount(successCount)
        .failureCount(failureCount)
        .complete(true)
        .lastUpdated(LocalDateTime.now())
        .build();
    aircraftTypeStatusRepository.save(newStatus);

    log.info("AircraftType 초기화 완료: 총 {}건, 성공 {}건, 실패 {}건", totalWebRecords, successCount, failureCount);
  }
}