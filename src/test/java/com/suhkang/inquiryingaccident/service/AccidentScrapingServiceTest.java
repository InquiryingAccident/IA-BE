package com.suhkang.inquiryingaccident.service;

import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;

import com.suhkang.inquiryingaccident.global.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("dev")
@Slf4j
class AccidentScrapingServiceTest {
  @Autowired
  AccidentScrapingService accidentScrapingService;

  @Test
  public void mainTest() {
    lineLog("테스트 시작");

//    LogUtil.timeLog(this::테스트_스크래핑);
    LogUtil.timeLog(this::테스트_1971_2년_스크래핑);

    lineLog("테스트 종료");
  }

  public void 테스트_스크래핑() {
    for(int i=1902 ; i<=2025 ; i++){
      accidentScrapingService.scrapeYear(i);
      lineLog(null);
      log.info("{} 년 파싱 완료", i);
      lineLog(null);
    }
  }

  public void 테스트_1971_2년_스크래핑(){
    accidentScrapingService.scrapeYear(1971);
    accidentScrapingService.scrapeYear(1972);
  }


}