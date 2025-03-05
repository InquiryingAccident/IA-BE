package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AircraftTypeScrapingService {

  private final OkHttpClient okHttpClient;

  public AircraftTypeScrapingService(OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  /**
   * 주어진 URL에서 항공기 타입 정보를 스크래핑하여 AircraftType 목록을 반환합니다.
   *
   * @param url 스크래핑할 페이지 URL (예: https://asn.flightsafety.org/asndb/types/CJ)
   * @param usageType 항공기 사용 유형 (예: COMMERCIAL 또는 CORPORATE)
   * @return 파싱된 AircraftType 객체 리스트
   */
  public List<AircraftType> scrapeAircraftTypes(String url, AircraftUsageType usageType) {
    List<AircraftType> result = new ArrayList<>();
    Request request = new Request.Builder()
        .url(url)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("URL {} 요청 실패: {}", url, response);
        return result;
      }
      String html = response.body().string();
      // Jsoup.parse(html, url)로 baseUri를 지정하면 상대 URL 처리에 도움이 됩니다.
      Document doc = Jsoup.parse(html, url);

      Element table = doc.getElementById("myTable");
      if (table == null) {
        log.warn("URL {} 에서 테이블을 찾을 수 없음", url);
        return result;
      }
      Elements rows = table.select("tbody tr");
      for (Element row : rows) {
        Elements cols = row.select("td");
        if (cols.size() < 3) {
          continue;
        }
        Element aTag = cols.get(0).selectFirst("a");
        if (aTag == null) continue;

        String modelName = aTag.text().trim();
        String href = aTag.attr("href").trim();
        // 코드: href에서 마지막 '/' 이후의 문자열
        String modelCode = "";
        int lastSlash = href.lastIndexOf("/");
        if (lastSlash != -1 && lastSlash < href.length() - 1) {
          modelCode = href.substring(lastSlash + 1);
        }
        // 첫 비행 연도: 두번째 컬럼 (빈 값이면 null)
        Integer firstFlightYear = null;
        String yearText = cols.get(1).text().trim();
        if (!yearText.isEmpty()) {
          try {
            firstFlightYear = Integer.parseInt(yearText);
          } catch (NumberFormatException e) {
            log.warn("첫 비행 연도 파싱 실패: {} (modelCode: {})", yearText, modelCode);
          }
        }
        // 상세설명: 세번째 컬럼
        String description = cols.get(2).text().trim();

        // 제조사는 modelName의 첫 단어로 추출 (예: "Airbus A220" → "Airbus")
        String manufacturer = "";
        if (!modelName.isEmpty()) {
          manufacturer = modelName.split(" ")[0];
        }
        // 상세 URL: href가 상대 URL이면 기본 도메인 추가
        String fullUrl = href.startsWith("test")
            ? href
            : "https://asn.flightsafety.org" + href;

        AircraftType aircraftType = new AircraftType();
        aircraftType.setModelCode(modelCode);
        aircraftType.setModelName(modelName);
        aircraftType.setManufacturer(manufacturer);
        aircraftType.setFirstFlightYear(firstFlightYear);
        aircraftType.setDescription(description);
        aircraftType.setAircraftUsageType(usageType);
        aircraftType.setAsnAccidentUrl(fullUrl);

        result.add(aircraftType);
      }
    } catch (IOException ex) {
      log.error("스케래핑 중 오류 발생: {}", url, ex);
    }
    return result;
  }
}
