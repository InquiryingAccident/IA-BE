package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.repository.AircraftRepository;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AircraftInitializationService {

  private final AircraftTypeRepository aircraftTypeRepository;
  private final AircraftRepository aircraftRepository;
  private final AircraftScrapingService scrapingService;

  @Transactional
  public void initDatabase() {
    List<AircraftType> aircraftTypes = aircraftTypeRepository.findAll();
    ExecutorService executor = Executors.newFixedThreadPool(4); // 병렬 처리 스레드 풀
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    for (AircraftType aircraftType : aircraftTypes) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        processAircraftType(aircraftType);
      }, executor);
      futures.add(future);
    }

    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
      log.info("모든 AircraftType에 대한 Aircraft 초기화 작업 완료.");
    } catch (Exception e) {
      log.error("Aircraft 초기화 중 병렬 작업 에러 발생", e);
      throw e;
    } finally {
      executor.shutdown();
    }
  }

  @Transactional
  public void processAircraftType(AircraftType aircraftType) {
    String modelCode = aircraftType.getModelCode();
    synchronized (modelCode.intern()) { // modelCode별 동기화
      if (aircraftRepository.existsByModelCode(modelCode)) {
        log.info("AircraftType {}: 이미 Aircraft 데이터 존재. 스크래핑 건너뜀", modelCode);
        return;
      }
      log.info("AircraftType {}: 새로운 Aircraft 데이터로 스크래핑 시작", modelCode);
      scrapingService.scrapeAndSaveAircraft(modelCode);
      log.info("AircraftType {}: Aircraft 정보 저장 완료", modelCode);
    }
  }
}