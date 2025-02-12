package com.suhkang.inquiryingaccident.global.init;

import com.suhkang.inquiryingaccident.object.constants.CommonStatus;
import com.suhkang.inquiryingaccident.object.dao.Accident;
import com.suhkang.inquiryingaccident.object.dao.AccidentDBYearStatus;
import com.suhkang.inquiryingaccident.repository.AccidentDBYearStatusRepository;
import com.suhkang.inquiryingaccident.repository.AccidentRepository;
import com.suhkang.inquiryingaccident.service.AccidentScrapingService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseInitializationService {

  private final AccidentRepository accidentRepository;
  private final AccidentDBYearStatusRepository accidentDBYearStatusRepository;
  private final AccidentScrapingService accidentScrapingService;
  @Qualifier("executorService")
  private final ExecutorService executorService;

  // 최소 파싱 시작 연도 (예: 1902)
  private static final int MIN_YEAR = 1902;
  // 현재 연도
  private static final int CURRENT_YEAR = LocalDateTime.now().getYear();

  /**
   * DB 초기화 및 연도별 파싱 상태를 병렬로 확인 및 업데이트합니다.
   * 각 연도별 작업은 별도의 트랜잭션으로 처리됩니다.
   */
  public void initializeOrUpdateDatabase() {

    // MIN_YEAR부터 CURRENT_YEAR까지 각 연도의 작업을 Callable로 생성
    List<Callable<Void>> tasks = IntStream.rangeClosed(MIN_YEAR, CURRENT_YEAR)
        .mapToObj(year -> (Callable<Void>) () -> {
          processYear(year);
          return null;
        })
        .collect(Collectors.toList());

    try {
      // 모든 작업을 병렬 실행하고, 완료될 때까지 대기
      List<Future<Void>> futures = executorService.invokeAll(tasks);
      for (Future<Void> future : futures) {
        future.get();
      }
      log.info("모든 연도에 대한 DB 초기화/업데이트 작업 완료.");
    } catch (InterruptedException | ExecutionException e) {
      log.error("병렬 작업 실행 중 에러 발생", e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * 연도별 DB 상태를 확인하여,
   *  - 상태 레코드가 없으면 전체 스크래핑 실행,
   *  - 상태 레코드가 있으나 complete가 false이면 기존 데이터를 삭제하고 재스크래핑,
   *  - 현재 연도라면 웹사이트 건수와 DB 건수를 비교하여 증분 스크래핑을 수행합니다.
   * 각 작업은 별도의 트랜잭션 내에서 실행됩니다.
   *
   * @param year 처리할 연도
   */
  @Transactional
  public void processYear(int year) {
    AccidentDBYearStatus status = accidentDBYearStatusRepository.findByYear(year).orElse(null);
    if (status == null) {
      log.info("연도 {}: 상태 레코드 없음 → 전체 스크래핑 실행", year);
      runYearScraping(year);
    } else {
      if (!status.isComplete()) {
        log.info("연도 {}: 파싱 상태 미완료(PENDING) → 기존 데이터 삭제 후 재스크래핑", year);
        deleteYearRecords(year);
        runYearScraping(year);
      } else {
        log.info("연도 {}: 이미 파싱 완료됨 (complete).", year);
        if (year == CURRENT_YEAR) {
          int websiteCount = fetchWebsiteRecordCountForYear(year);
          int dbCount = accidentRepository.countByAccidentYear(year);
          if (dbCount < websiteCount) {
            log.info("연도 {}: DB 기록 {}건, 웹사이트 기록 {}건 → 증분 스크래핑 실행", year, dbCount, websiteCount);
            runIncrementalScrapingForYear(year, dbCount);
          } else {
            log.info("연도 {}: DB 기록과 웹사이트 기록이 동일 → 업데이트 불필요", year);
          }
        }
      }
    }
  }

  /**
   * 연도별 전체 스크래핑 실행
   */
  private void runYearScraping(int year) {
    try {
      accidentScrapingService.scrapeYear(year);
      // 스크래핑 후 DB에 저장된 사고 건수를 기준으로 상태 업데이트
      int total = accidentRepository.countByAccidentYear(year);
      int success = accidentRepository.countByAccidentYearAndCommonStatus(year, CommonStatus.SUCCESS);
      int failure = accidentRepository.countByAccidentYearAndCommonStatus(year, CommonStatus.FAILURE);
      AccidentDBYearStatus status = AccidentDBYearStatus.builder()
          .year(year)
          .totalRecords(total)
          .successCount(success)
          .failureCount(failure)
          .complete(true)
          .lastUpdated(LocalDateTime.now())
          .build();
      accidentDBYearStatusRepository.save(status);
      log.info("연도 {} 스크래핑 완료. 총 {}건 (SUCCESS {}건, FAILURE {}건)", year, total, success, failure);
    } catch (Exception e) {
      log.error("연도 {} 스크래핑 중 에러 발생: {}", year, e.getMessage());
      AccidentDBYearStatus status = AccidentDBYearStatus.builder()
          .year(year)
          .complete(false)
          .lastUpdated(LocalDateTime.now())
          .build();
      accidentDBYearStatusRepository.save(status);
    }
  }

  /**
   * 미완료된 연도의 기존 사고 데이터 삭제
   */
  private void deleteYearRecords(int year) {
    List<Accident> accidents = accidentRepository.findByAccidentYear(year);
    accidentRepository.deleteAll(accidents);
    log.info("연도 {}의 기존 {} 건의 사고 데이터 삭제", year, accidents.size());
  }

  /**
   * 웹사이트에서 해당 연도의 총 사고 건수를 가져오는 메서드
   * 실제 구현에서는 Jsoup 등을 사용하여 페이지 상의 "25 occurrences" 등의 값을 파싱합니다.
   * 여기서는 예시로 25를 반환하도록 하였습니다.
   *
   * @param year 해당 연도
   * @return 사고 건수 (예시: 25)
   */
  private int fetchWebsiteRecordCountForYear(int year) {
    // TODO: 실제 스크래핑 로직 구현 (예: Jsoup으로 페이지 파싱)
    return 25;
  }

  /**
   * 현재 연도에 대해 DB에 저장된 건수 이후의 사고만 파싱하는 증분 스크래핑
   *
   * @param year 현재 연도
   * @param existingCount DB에 이미 저장된 사고 건수
   */
  private void runIncrementalScrapingForYear(int year, int existingCount) {
    accidentScrapingService.scrapeYear(year);
    int total = accidentRepository.countByAccidentYear(year);
    int success = accidentRepository.countByAccidentYearAndCommonStatus(year, CommonStatus.SUCCESS);
    int failure = accidentRepository.countByAccidentYearAndCommonStatus(year, CommonStatus.FAILURE);
    AccidentDBYearStatus status = accidentDBYearStatusRepository.findByYear(year)
        .orElse(AccidentDBYearStatus.builder().year(year).build());
    status.setTotalRecords(total);
    status.setSuccessCount(success);
    status.setFailureCount(failure);
    status.setLastUpdated(LocalDateTime.now());
    status.setComplete(true);
    accidentDBYearStatusRepository.save(status);
    log.info("연도 {} 증분 업데이트 완료. DB 기록 {}건 (SUCCESS {}건, FAILURE {}건)", year, total, success, failure);
  }
}
