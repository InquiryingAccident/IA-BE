package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.global.util.CommonUtil;
import com.suhkang.inquiryingaccident.object.constants.AircraftRegistrationCode;
import com.suhkang.inquiryingaccident.object.constants.CommonStatus;
import com.suhkang.inquiryingaccident.object.dao.Accident;
import com.suhkang.inquiryingaccident.repository.AccidentRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccidentScrapingService {

  private final OkHttpClient okHttpClient;
  private final AccidentRepository accidentRepository;

  private static final String BASE_URL = "https://asn.flightsafety.org";

  /**
   * 주어진 연도의 사고 데이터를 스크랩합니다.
   * 페이지 번호 1부터 시작하여, "no occurrences in the database" 메시지가 나오면 중단합니다.
   *
   * @param year 스크래핑할 연도 (예: 2025)
   */
  public void scrapeYear(int year) {
    int page = 1;
    while (true) {
      String url = BASE_URL + "/database/year/" + year + "/" + page;
      log.info("Scraping {} ...", url);
      try {
        String html = fetchHtml(url);
        if (html == null || html.isEmpty()) {
          log.info("빈 페이지 혹은 요청 실패: {}", url);
          break;
        }
        log.debug("HTML snippet: {}", html.substring(0, Math.min(200, html.length())));
        Document doc = Jsoup.parse(html);
        log.debug("Document title: {}", doc.title());

        if (doc.text().toLowerCase().contains("no occurrences in the database")) {
          log.info("더 이상 데이터 없음. 페이지 종료.");
          break;
        }

        Element table = doc.selectFirst("table.hp");
        if (table == null) {
          log.warn("사고 데이터 테이블을 찾을 수 없음: {}", url);
          break;
        }
        Elements rows = table.select("tr.list");
        if (rows.isEmpty()) {
          log.info("사고 행이 없습니다. 페이지 종료: {}", url);
          break;
        }
        for (Element row : rows) {
          log.debug("Processing row: {}", row.text());
          Accident accident = parseAccidentRow(row);
          if (accident != null) {
            accidentRepository.save(accident);
            log.debug("Saved accident: {}", accident);
          } else {
            log.warn("Failed to parse accident row: {}", row.text());
          }
        }
        log.info("페이지 {} : {} 건 스크랩됨.", page, rows.size());
        page++;
      } catch (Exception e) {
        log.error("스크래핑 중 에러 발생", e);
        break;
      }
    }
  }

  private String fetchHtml(String url) throws IOException {
    Request request = new Request.Builder()
        .url(url)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      if (response.isSuccessful() && response.body() != null) {
        String body = response.body().string();
        if (body.trim().isEmpty()) {
          log.warn("응답 본문이 비어 있습니다: {}", url);
          return null;
        }
        return body;
      } else {
        log.error("요청 실패: {} - 응답 코드: {}", url, response.code());
        return null;
      }
    }
  }

  private Accident parseAccidentRow(Element row) {
    try {
      Elements cells = row.select("td");
      if (cells.size() < 9) {
        log.warn("셀 개수가 부족합니다: {}", row.text());
        return null;
      }

      Accident.AccidentBuilder accidentBuilder = Accident.builder();

      // 1. 사고 날짜와 wikibase id
      String dateStr = cells.get(0).text().trim();
      log.debug("Accident date string: {}", dateStr);
      LocalDate accidentDate = parseAccidentDate(dateStr, accidentBuilder);
      accidentBuilder.accidentDate(accidentDate);

      String wikibaseId = null;
      Element link = cells.get(0).selectFirst("a");
      if (link != null) {
        String href = link.attr("href");
        String[] parts = href.split("/");
        if (parts.length > 0) {
          wikibaseId = parts[parts.length - 1];
        }
      }
      accidentBuilder.wikibaseId(wikibaseId);

      // 2. 항공기 기종, 등록번호, 운영자
      String aircraftType = cells.get(1).text().trim();
      String registration = cells.get(2).text().trim();
      String operator = cells.get(3).text().trim();
      accidentBuilder.aircraftType(aircraftType)
          .registration(registration)
          .operator(operator);
      log.debug("AircraftType: {}, Registration: {}, Operator: {}",
          aircraftType, registration, operator);

      // 3. 사상자 (fatalities) – “0+1”, “6+1” 등 합산
      String fatalitiesStr = cells.get(4).text().trim();
      Integer fatalities = 0;
      try {
        if (fatalitiesStr.contains("+")) {
          String[] parts = fatalitiesStr.split("\\+");
          for (String part : parts) {
            fatalities += Integer.parseInt(part.trim());
          }
        } else if (CommonUtil.nvl(fatalitiesStr).equals("")) {
          fatalities = null;
        } else {
          fatalities = Integer.parseInt(fatalitiesStr);
        }
      } catch (NumberFormatException e) {
        fatalities = null;
        log.warn("Fatalities 파싱 실패: '{}'", fatalitiesStr);
      }
      accidentBuilder.fatalities(fatalities);

      // 4. 위치
      String location = cells.get(5).text().trim();
      accidentBuilder.location(location);
      log.debug("Location: {}", location);

      // 5. 국기 이미지에서 국가 코드 추출
      String countryCode = null;
      Element flagImg = cells.get(6).selectFirst("img");
      if (flagImg != null) {
        String src = flagImg.attr("src");
        String filename = src.substring(src.lastIndexOf('/') + 1);
        if (filename.contains(".")) {
          countryCode = filename.substring(0, filename.indexOf('.'));
        } else {
          countryCode = filename;
        }
      }
      log.debug("Extracted country code: {}", countryCode);

      // 6. 피해 정도
      String damage = cells.get(7).text().trim();
      accidentBuilder.damage(damage);
      log.debug("Damage: {}", damage);

      // 7. 예비 조사 보고서 아이콘 존재 여부
      boolean hasPreliminaryReport = cells.get(8).selectFirst("img") != null;
      accidentBuilder.hasPreliminaryReport(hasPreliminaryReport);
      log.debug("Has preliminary report: {}", hasPreliminaryReport);

      // 문자열 countryCode -> AircraftRegistrationCode Enum 변환
      AircraftRegistrationCode registrationCode = null;
      if (countryCode != null && !countryCode.isEmpty()) {
        try {
          registrationCode = AircraftRegistrationCode.fromString(countryCode);
        } catch (IllegalArgumentException ex) {
          log.warn("등록 코드 '{}' 를 매핑하는 Enum을 찾을 수 없음", countryCode);
        }
      }
      accidentBuilder.aircraftRegistrationCode(registrationCode);

      if (accidentBuilder.build().getCommonStatus() == null) {
        accidentBuilder.commonStatus(CommonStatus.SUCCESS);
      }

      return accidentBuilder.build();
    } catch (Exception e) {
      log.error("사고 행 파싱 중 에러: {}", row.text(), e);
      return Accident.builder()
          .errorMessage("파싱 오류: " + e.getMessage())
          .commonStatus(CommonStatus.FAILURE)
          .build();
    }
  }

  private LocalDate parseAccidentDate(String dateStr, Accident.AccidentBuilder accidentBuilder) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
    try {
      return LocalDate.parse(dateStr, formatter);
    } catch (Exception e) {
      log.warn("날짜 포맷 파싱 실패: '{}'. 에러: {}", dateStr, e.getMessage());
      Pattern yearPattern = Pattern.compile("(\\d{4})");
      Matcher yearMatcher = yearPattern.matcher(dateStr);
      int year;
      if (yearMatcher.find()) {
        year = Integer.parseInt(yearMatcher.group(1));
      } else {
        throw new RuntimeException("연도 정보 없음: " + dateStr, e);
      }
      int month = 1;
      Pattern monthPattern = Pattern.compile("(?i)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)");
      Matcher monthMatcher = monthPattern.matcher(dateStr);
      if (monthMatcher.find()) {
        String monthStr = monthMatcher.group(1);
        try {
          month = LocalDate.parse("1 " + monthStr + " " + year,
                  DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH))
              .getMonthValue();
        } catch (Exception ex) {
          log.warn("월 파싱 실패: '{}', 기본값 1월 사용", monthStr);
          month = 1;
        }
      }
      LocalDate fallbackDate = LocalDate.of(year, month, 1);
      String errMsg = "날짜 파싱 오류: 입력값 '" + dateStr + "' -> 대체값 " + fallbackDate;
      log.warn(errMsg);
      accidentBuilder.errorMessage(errMsg);
      accidentBuilder.commonStatus(CommonStatus.FAILURE);
      return fallbackDate;
    }
  }
}
