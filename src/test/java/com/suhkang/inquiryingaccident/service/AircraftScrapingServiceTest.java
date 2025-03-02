package com.suhkang.inquiryingaccident.service;

import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.superLog;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.timeLog;

import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.repository.AircraftRepository;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Profile("dev")
@Transactional
@Slf4j
class AircraftScrapingServiceTest {

  @Autowired
  AircraftScrapingService aircraftScrapingService;
  @Autowired
  AircraftRepository aircraftRepository;
  @Autowired
  AircraftTypeRepository aircraftTypeRepository;

  @Test
  public void mainTest() {
    lineLog("테스트 시작");

//    LogUtil.timeLog(this::테스트_항공기타입_저장);
    timeLog(this::테스트_스크래핑);

    lineLog("테스트 종료");
  }

  public void 테스트_스크래핑() throws InterruptedException {
    lineLog(null);
//    Thread.sleep(1000);
    Aircraft aircraft = aircraftScrapingService.scrapeAndSaveAircraft("A320");
    superLog(aircraft);
    lineLog(null);
  }

  public void 테스트_항공기타입_저장() throws InterruptedException {
    lineLog(null);
    // AircraftType 데이터 삽입
    AircraftType aircraftType = AircraftType.builder()
        .modelCode("A320")
        .manufacturer("Airbus")
        .modelName("A320")
        .aircraftUsageType(AircraftUsageType.COMMERCIAL)
        .asnAccidentUrl("https://example.com")
        .build();
    AircraftType saved = aircraftTypeRepository.save(aircraftType);
    superLog(saved);
    lineLog(null);
  }


}
