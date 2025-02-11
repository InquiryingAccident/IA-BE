package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.constants.AircraftRegistrationCode;
import com.suhkang.inquiryingaccident.object.dao.Accident;
import com.suhkang.inquiryingaccident.repository.AccidentRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
        // HTML 일부 내용 디버그 로그 출력 (최대 200자)
        log.debug("HTML snippet: {}", html.substring(0, Math.min(200, html.length())));

        Document doc = Jsoup.parse(html);
        log.debug("Document title: {}", doc.title());

        // "no occurrences in the database" 문구 확인
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

  /**
   * 각 사고 행(tr.list)을 파싱하여 Accident 엔티티로 변환합니다.
   *
   * 테이블 컬럼 순서:
   * <ol>
   *   <li>acc. date (날짜 및 wikibase 링크)</li>
   *   <li>aircraft type</li>
   *   <li>registration</li>
   *   <li>operator</li>
   *   <li>fatalities</li>
   *   <li>location</li>
   *   <li>국기 이미지 (국가 코드 추출)</li>
   *   <li>damage</li>
   *   <li>예비 조사 보고서 아이콘</li>
   * </ol>
   *
   * @param row HTML tr 요소
   * @return Accident 엔티티 (파싱 실패 시 null)
   */
  private Accident parseAccidentRow(Element row) {
    try {
      Elements cells = row.select("td");
      if (cells.size() < 9) {
        log.warn("셀 개수가 부족합니다: {}", row.text());
        return null;
      }

      // 1. 사고 날짜와 wikibase id
      String dateStr = cells.get(0).text().trim();
      log.debug("Accident date string: {}", dateStr);
      // 날짜 포맷: "2 Jan 2025" → Locale.ENGLISH 지정
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
      LocalDate accidentDate = LocalDate.parse(dateStr, formatter);
      log.debug("Parsed accident date: {}", accidentDate);

      String wikibaseId = null;
      Element link = cells.get(0).selectFirst("a");
      if (link != null) {
        String href = link.attr("href");  // 예: "/wikibase/469640"
        String[] parts = href.split("/");
        if (parts.length > 0) {
          wikibaseId = parts[parts.length - 1];
        }
      }
      log.debug("Extracted wikibaseId: {}", wikibaseId);

      // 2. 항공기 기종, 등록번호, 운영자
      String aircraftType = cells.get(1).text().trim();
      String registration = cells.get(2).text().trim();
      String operator = cells.get(3).text().trim();
      log.debug("AircraftType: {}, Registration: {}, Operator: {}",
          aircraftType, registration, operator);

      // 3. 사상자 (fatalities)
      String fatalitiesStr = cells.get(4).text().trim();
      Integer fatalities = 0;
      try {
        fatalities = Integer.parseInt(fatalitiesStr);
      } catch (NumberFormatException e) {
        log.warn("Fatalities 파싱 실패: '{}'", fatalitiesStr);
      }
      log.debug("Parsed fatalities: {}", fatalities);

      // 4. 위치
      String location = cells.get(5).text().trim();
      log.debug("Location: {}", location);

      // 5. 국기 이미지에서 국가 코드 추출 (문자열 추출)
      String countryCode = null;
      Element flagImg = cells.get(6).selectFirst("img");
      if (flagImg != null) {
        String src = flagImg.attr("src"); // 예: .../N.gif
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
      log.debug("Damage: {}", damage);

      // 7. 예비 조사 보고서 아이콘 존재 여부
      boolean hasPreliminaryReport = cells.get(8).selectFirst("img") != null;
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

      return Accident.builder()
          .wikibaseId(wikibaseId)
          .accidentDate(accidentDate)
          .aircraftType(aircraftType)
          .registration(registration)
          .operator(operator)
          .fatalities(fatalities)
          .location(location)
          .aircraftRegistrationCode(registrationCode)
          .damage(damage)
          .hasPreliminaryReport(hasPreliminaryReport)
          .build();
    } catch (Exception e) {
      log.error("사고 행 파싱 중 에러: {}", row.text(), e);
      return null;
    }
  }
}
