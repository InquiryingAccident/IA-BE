package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.object.dao.AircraftTypeStatus;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import com.suhkang.inquiryingaccident.repository.AircraftTypeStatusRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AircraftTypeInitializationService {

  private final AircraftTypeRepository aircraftTypeRepository;
  private final AircraftTypeStatusRepository aircraftTypeStatusRepository;
  private final AircraftTypeScrapingService aircraftTypeScrapingService;

  private static final String COMMERCIAL_JET_INFO_URL = "https://asn.flightsafety.org/asndb/types/CJ";
  private static final String CORPORATE_JET_INFO_URL = "https://asn.flightsafety.org/asndb/types/BJ";

  @Transactional
  public void initDatabase() {
    // 1. 현재 DB 상태 확인
    AircraftTypeStatus latestStatus = aircraftTypeStatusRepository.findTopByOrderByLastUpdatedDesc()
        .orElse(null);

    // 2. 웹에서 AircraftType 데이터 가져오기
    log.info("항공기 타입 웹 데이터 스크래핑 시작 - 상업용 항공기");
    List<AircraftType> commercialTypes = aircraftTypeScrapingService.scrapeAircraftTypes(
        COMMERCIAL_JET_INFO_URL, AircraftUsageType.COMMERCIAL);
    log.info("항공기 타입 웹 데이터 스크래핑 시작 - 기업용 항공기");
    List<AircraftType> corporateTypes = aircraftTypeScrapingService.scrapeAircraftTypes(
        CORPORATE_JET_INFO_URL, AircraftUsageType.CORPORATE);
    int totalWebRecords = commercialTypes.size() + corporateTypes.size();

    // 3. DB에 저장된 AircraftType 개수 확인
    long dbRecordCount = aircraftTypeRepository.count();
    log.info("현재 DB에 저장된 항공기 타입: {}개", dbRecordCount);

    // 4. 변경 여부 판단: 웹 데이터 개수와 DB 개수가 다르거나, 상태가 없거나 완료되지 않은 경우
    boolean needsReparsing = latestStatus == null || !latestStatus.isComplete() || dbRecordCount != totalWebRecords;

    if (!needsReparsing) {
      log.info("AircraftType 데이터가 최신 상태입니다. (웹: {}, DB: {}). 파싱 건너뜀", totalWebRecords, dbRecordCount);
      return;
    }

    log.info("AircraftType 데이터 변경 감지 (웹: {}, DB: {}). 전체 파싱 시작", totalWebRecords, dbRecordCount);

    // 5. 모든 항공기 타입을 하나의 리스트로 결합
    List<AircraftType> allNewTypes = new ArrayList<>();
    allNewTypes.addAll(commercialTypes);
    allNewTypes.addAll(corporateTypes);
    log.debug("스크래핑된 총 항공기 타입: {}개", allNewTypes.size());

    // 6. 기존 데이터 가져오기
    List<AircraftType> existingTypes = aircraftTypeRepository.findAll();
    log.debug("DB에서 불러온 기존 항공기 타입: {}개", existingTypes.size());

    // 기존 데이터를 식별자로 매핑 (modelCode를 키로 사용)
    Map<String, AircraftType> existingTypeMap = new HashMap<>();
    for (AircraftType type : existingTypes) {
      String key = type.getModelCode();
      if (existingTypeMap.containsKey(key)) {
        log.warn("중복된 modelCode를 가진 항공기 타입 발견: {}, ID: {}", key, type.getAircraftTypeId());
      }
      existingTypeMap.put(key, type);
    }
    log.debug("기존 항공기 타입 modelCode 매핑 완료: {}개", existingTypeMap.size());

    // 7. 데이터 업데이트/추가
    int updateCount = 0;
    int insertCount = 0;
    int errorCount = 0;

    // 새 데이터 처리 (업데이트 또는 삽입)
    for (AircraftType newType : allNewTypes) {
      try {
        String modelCode = newType.getModelCode();
        log.debug("처리 중인 항공기 타입: {}, modelCode: {}", newType.getModelName(), modelCode);

        if (existingTypeMap.containsKey(modelCode)) {
          // 기존 데이터가 있으면 ID 유지하면서 업데이트
          AircraftType existingType = existingTypeMap.get(modelCode);
          log.debug("기존 항공기 타입 발견, ID 유지 후 업데이트: {}, ID: {}",
              modelCode, existingType.getAircraftTypeId());

          // 업데이트 전 정보 로깅
          String beforeUpdate = String.format("업데이트 전: 제조사=%s, 모델명=%s, 연도=%d, 설명=%s",
              existingType.getManufacturer(), existingType.getModelName(),
              existingType.getFirstFlightYear(), existingType.getDescription());

          updateAircraftTypeFields(existingType, newType); // ID는 유지하고 데이터만 업데이트

          // 업데이트 후 정보 로깅
          String afterUpdate = String.format("업데이트 후: 제조사=%s, 모델명=%s, 연도=%d, 설명=%s",
              existingType.getManufacturer(), existingType.getModelName(),
              existingType.getFirstFlightYear(), existingType.getDescription());

          log.debug("{} -> {}", beforeUpdate, afterUpdate);

          aircraftTypeRepository.save(existingType);
          updateCount++;
        } else {
          // 새 데이터면 삽입
          log.debug("새로운 항공기 타입, 신규 등록: {}", modelCode);
          aircraftTypeRepository.save(newType);
          insertCount++;
        }
      } catch (Exception e) {
        errorCount++;
        log.error("AircraftType 저장 실패: {}, 오류: {}",
            newType.getModelName(), e.getMessage(), e);
      }
    }

    log.info("항공기 타입 업데이트 처리 결과: 업데이트 {}개, 신규 등록 {}개, 오류 {}개",
        updateCount, insertCount, errorCount);

    // 8. 웹에서 가져온 데이터에 존재하지 않는 기존 레코드는
    // 참조 관계가 없는 경우에만 삭제 시도 (외래 키 제약 조건 위반 방지)
    Set<String> newTypeModelCodes = allNewTypes.stream()
        .map(AircraftType::getModelCode)
        .collect(Collectors.toSet());

    int deletedCount = 0;
    int skipDeleteCount = 0;

    for (AircraftType existingType : existingTypes) {
      String modelCode = existingType.getModelCode();
      if (!newTypeModelCodes.contains(modelCode)) {
        try {
          log.debug("더 이상 사용되지 않는 항공기 타입 삭제 시도: {}, ID: {}",
              modelCode, existingType.getAircraftTypeId());
          aircraftTypeRepository.delete(existingType);
          deletedCount++;
          log.info("항공기 타입 삭제 성공: {}, ID: {}",
              modelCode, existingType.getAircraftTypeId());
        } catch (Exception e) {
          skipDeleteCount++;
          log.warn("항공기 타입 삭제 실패 (참조 중): {}, ID: {}, 오류: {}",
              modelCode, existingType.getAircraftTypeId(), e.getMessage());
          log.debug("이 항공기 타입은 다른 테이블에서 참조 중이므로 삭제할 수 없습니다. 유지됩니다.");
        }
      }
    }

    log.info("더 이상 사용되지 않는 항공기 타입 처리: 삭제 {}개, 삭제 실패(참조 중) {}개",
        deletedCount, skipDeleteCount);

    // 9. 상태 업데이트
    AircraftTypeStatus newStatus = AircraftTypeStatus.builder()
        .totalRecords(totalWebRecords)
        .successCount(updateCount + insertCount)
        .failureCount(errorCount + skipDeleteCount)
        .complete(true)
        .lastUpdated(LocalDateTime.now())
        .build();
    aircraftTypeStatusRepository.save(newStatus);

    log.info("AircraftType 초기화 완료: 총 {}개 웹 데이터, 업데이트 {}개, 삽입 {}개, 삭제 {}개, 유지(참조 중) {}개, 오류 {}개",
        totalWebRecords, updateCount, insertCount, deletedCount, skipDeleteCount, errorCount);
  }

  /**
   * 기존 항공기 타입의 필드를 새 데이터로 업데이트합니다.
   * ID는 유지하고 다른 데이터만 업데이트합니다.
   */
  private void updateAircraftTypeFields(AircraftType existingType, AircraftType newType) {
    // ID는 유지 (aircraftTypeId)
    // modelCode도 동일할 것이므로 업데이트할 필요 없음
    existingType.setManufacturer(newType.getManufacturer());
    existingType.setModelName(newType.getModelName());
    existingType.setFirstFlightYear(newType.getFirstFlightYear());
    existingType.setDescription(newType.getDescription());
    existingType.setAircraftUsageType(newType.getAircraftUsageType());
    existingType.setAsnAccidentUrl(newType.getAsnAccidentUrl());
  }
}