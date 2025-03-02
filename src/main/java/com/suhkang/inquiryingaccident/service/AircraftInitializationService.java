package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.global.object.HashRegistryRepository;
import com.suhkang.inquiryingaccident.global.util.CommonUtil;
import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.repository.AircraftRepository;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AircraftInitializationService {

  private final AircraftRepository aircraftRepository;
  private final AircraftTypeRepository aircraftTypeRepository;
  private final HashRegistryRepository hashRegistryRepository;
  private final AircraftScrapingService aircraftScrapingService;
  @Qualifier("executorService")
  private final ExecutorService executorService;

  /**
   * 모든 AircraftType에 대해 Aircraft 정보를 초기화합니다.
   * 병렬로 처리하며, 이미 저장된 데이터는 패스합니다.
   */
  public void initDatabase() {
    List<AircraftType> aircraftTypes = aircraftTypeRepository.findAll();
    log.info("총 {}개의 AircraftType 발견", aircraftTypes.size());

    List<Callable<Void>> tasks = aircraftTypes.stream()
        .map(aircraftType -> (Callable<Void>) () -> {
          processAircraftType(aircraftType);
          return null;
        })
        .collect(Collectors.toList());

    try {
      List<Future<Void>> futures = executorService.invokeAll(tasks);
      for (Future<Void> future : futures) {
        future.get();
      }
      log.info("모든 AircraftType에 대한 Aircraft 초기화 작업 완료.");
    } catch (InterruptedException | ExecutionException e) {
      log.error("Aircraft 초기화 중 병렬 작업 에러 발생", e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * 단일 AircraftType에 대해 Aircraft 정보를 처리합니다.
   * modelCode 기반으로 중복 여부를 확인하고, 필요한 경우 스크래핑 실행.
   */
  @Transactional
  public void processAircraftType(AircraftType aircraftType) {
    String modelCode = aircraftType.getModelCode();

    // 이미 Aircraft가 존재하면 건너뜀
    if (aircraftRepository.existsByModelCode(modelCode)) {
      log.info("AircraftType {}: 이미 Aircraft 데이터 존재. 스크래핑 건너뜀", modelCode);
      return;
    }

    log.info("AircraftType {}: 새로운 Aircraft 데이터로 스크래핑 시작", modelCode);
    Aircraft aircraft = aircraftScrapingService.scrapeAndSaveAircraft(modelCode);
    if (aircraft != null) {
      log.info("AircraftType {}: Aircraft 정보 저장 완료", modelCode);
    } else {
      log.warn("AircraftType {}: 스크래핑 실패", modelCode);
    }
  }

  /**
   * AircraftType 정보를 기반으로 해시값을 계산합니다.
   * modelCode, manufacturer, modelName 등을 조합하여 고유성을 보장.
   */
  private String calculateAircraftHash(AircraftType aircraftType) {
    String data = aircraftType.getModelCode() +
        aircraftType.getManufacturer() +
        aircraftType.getModelName() +
        aircraftType.getFirstFlightYear() +
        aircraftType.getDescription() +
        aircraftType.getAircraftUsageType() +
        aircraftType.getAsnAccidentUrl();
    return CommonUtil.calculateSha256ByStr(data);
  }
}