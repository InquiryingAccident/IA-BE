package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.util.LogUtil;
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
    LogUtil.timeLog(this::테스트_스크래핑);
  }

  public void 테스트_스크래핑() {
    for(int i=1902 ; i<=2025 ; i++){
      accidentScrapingService.scrapeYear(i);
      LogUtil.lineLog(null);
      log.info("{} 년 파싱 완료", i);
      LogUtil.lineLog(null);
    }
  }
}