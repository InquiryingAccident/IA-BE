package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.global.exception.CustomException;
import com.suhkang.inquiryingaccident.global.exception.ErrorCode;
import com.suhkang.inquiryingaccident.global.util.CommonUtil;
import com.suhkang.inquiryingaccident.global.util.ScrapingUtil;
import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import com.suhkang.inquiryingaccident.object.dao.AircraftType;
import com.suhkang.inquiryingaccident.repository.AircraftRepository;
import com.suhkang.inquiryingaccident.repository.AircraftTypeRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AircraftScrapingService {

  private final AircraftRepository aircraftRepository;
  private final AircraftTypeRepository aircraftTypeRepository;
  private final OkHttpClient okHttpClient;

  /**
   * 모델 코드(예: "A320")를 받아서 해당 상세 페이지를 스크래핑한 후 Aircraft 엔티티로 저장합니다.
   *
   * @param modelCode AircraftType에 저장된 모델 코드 (예: "A320")
   * @return 저장된 Aircraft 객체 (실패 시 null)
   */
  public Aircraft scrapeAndSaveAircraft(String modelCode) {
    // 기존 AircraftType DB에서 기본 항공기 정보를 조회
    AircraftType aircraftType = aircraftTypeRepository.findByModelCode(modelCode)
        .orElseThrow(() -> new CustomException(ErrorCode.AIRCRAFT_TYPE_NOT_FOUND));

    // 상세 페이지 URL 구성
    String url = "https://contentzone.eurocontrol.int/aircraftperformance/details.aspx?ICAO=" + modelCode + "&";
    Request request = new Request.Builder()
        .url(url)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
        .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("Failed to fetch URL: {} Response: {}", url, response);
        return null;
      }
      String html = response.body().string();
      Document doc = Jsoup.parse(html, url);

      // 기본 항공기 상세 정보
      String icaoLabel = ScrapingUtil.getTextById(doc, "MainContent_wsICAOLabel");
      String acftName = ScrapingUtil.getTextById(doc, "MainContent_wsAcftNameLabel");
      String manufacturer = ScrapingUtil.getTextById(doc, "MainContent_wsManufacturerLabel");

      // Main 이미지 URL
      String mainImageUrl = ScrapingUtil.getAttrById(doc, "MainContent_wsDrawing", "src");

      // 추가 이미지 URL 목록
      List<String> additionalImageUrls = new ArrayList<>();
      Element photosContainer = doc.getElementById("detailShowAllPhotos");
      if (photosContainer != null) {
        Elements imgs = photosContainer.select("img");
        for (Element img : imgs) {
          String src = img.attr("src");
          if (src != null && !src.isEmpty()) {
            additionalImageUrls.add(src);
          }
        }
      }

      // Performance 데이터 파싱
      Integer takeOffV2 = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsV2Literal"));
      Integer takeOffDistance = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsFARTOLiteral"));
      String takeOffWTC = ScrapingUtil.getTextById(doc, "wsWTCLiteral");
      String takeOffRECAT = ScrapingUtil.getTextById(doc, "wsRECATLiteral");
      Integer takeOffMTOW = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsMTOWLiteral"));

      Integer initialClimbIAS = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsINVCLLiteral"));
      Integer initialClimbROC = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsINROCLiteral"));

      Integer climb150IAS = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsIASVCLLiteral"));
      Integer climb150ROC = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsIASROCLiteral"));

      Integer climb240IAS = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsIASVCLiteral2"));
      Integer climb240ROC = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsIASROC2Literal"));

      Double machClimbMACH = CommonUtil.parseDouble(ScrapingUtil.getTextById(doc, "wsMACHVCLLiteral"));
      Integer machClimbROC = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsMACHROCLiteral"));

      Integer cruiseTAS = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsVCSknotsLiteral"));
      Double cruiseMACH = CommonUtil.parseDouble(ScrapingUtil.getTextById(doc, "wsVCSmachLiteral"));
      Integer cruiseCeiling = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsCeilingLiteral"));
      Integer cruiseRange = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsRangeLiteral"));

      Double initialDescentMACH = CommonUtil.parseDouble(ScrapingUtil.getTextById(doc, "wsMACHVDESCLiteral"));
      Integer initialDescentROD = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsMACHRODLiteral"));

      Integer descentIAS = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsIASVDESCLiteral"));
      Integer descentROD = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsIASRODLiteral"));

      Integer approachIAS = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsBelowVDESCLiteral"));
      Integer approachROD = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsBelowRODLiteral"));
      Integer approachMCS = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsMCSLiteral"));

      Integer landingVat = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsVTHLiteral"));
      Integer landingDistance = CommonUtil.parseInteger(ScrapingUtil.getTextById(doc, "wsFARLDLiteral"));
      String landingAPC = ScrapingUtil.getTextById(doc, "wsAPCLiteral");

      // Type of Aircraft 정보
      String type = ScrapingUtil.getTextById(doc, "MainContent_wsTypeLabel");
      String typeAPC = ScrapingUtil.getTextById(doc, "MainContent_wsAPCLabel");
      String typeWTC = ScrapingUtil.getTextById(doc, "MainContent_wsWTCLabel");
      String typeRecat = ScrapingUtil.getTextById(doc, "MainContent_wsRecatEULabel");

      // Technical 정보
      Double wingSpan = CommonUtil.parseDouble(CommonUtil.removeUnit(ScrapingUtil.getTextById(doc, "MainContent_wsLabelWingSpan")));
      Double length = CommonUtil.parseDouble(CommonUtil.removeUnit(ScrapingUtil.getTextById(doc, "MainContent_wsLabelLength")));
      Double height = CommonUtil.parseDouble(CommonUtil.removeUnit(ScrapingUtil.getTextById(doc, "MainContent_wsLabelHeight")));
      String powerPlant = ScrapingUtil.getTextById(doc, "MainContent_wsLabelPowerPlant");

      // Recognition 정보
      String wingPosition = ScrapingUtil.getTextById(doc, "MainContent_wsLabelWingPosition");
      String enginePosition = ScrapingUtil.getTextById(doc, "MainContent_wsLabelEngineData");
      String tailConfiguration = ScrapingUtil.getTextById(doc, "MainContent_wsLabelTailPosition");
      String landingGear = ScrapingUtil.getTextById(doc, "MainContent_wsLabelLandingGear");
      String recognitionSimilarity = "";
      Element recognitionEl = doc.getElementById("MainContent_wsLabelAlternativeNames");
      if (recognitionEl != null) {
        recognitionSimilarity = recognitionEl.text();
      }

      // Supplementary 정보
      String iataCode = ScrapingUtil.getTextById(doc, "MainContent_wsIATACode");
      String accommodation = ScrapingUtil.getTextById(doc, "MainContent_wsLabelAccommodation");
      String supplementaryNotes = ScrapingUtil.getTextById(doc, "MainContent_wsLabelNotes");
      String alternativeNamesStr = "";
      Element altNamesEl = doc.getElementById("MainContent_wsLabelAlternativeNames");
      if (altNamesEl != null) {
        alternativeNamesStr = altNamesEl.text();
      }
      List<String> alternativeNames = new ArrayList<>();
      if (!alternativeNamesStr.isEmpty()) {
        String[] parts = alternativeNamesStr.split("[;\\n]");
        for (String part : parts) {
          if (!part.trim().isEmpty()) {
            alternativeNames.add(part.trim());
          }
        }
      }

      // Aircraft 생성
      Aircraft aircraft = Aircraft.builder()
          // 기본 항공기 정보 (AircraftType에서 가져옴)
          .modelCode(aircraftType.getModelCode())
          .manufacturer(aircraftType.getManufacturer())
          .modelName(aircraftType.getModelName())
          .firstFlightYear(aircraftType.getFirstFlightYear())
          .description(aircraftType.getDescription())
          .aircraftUsageType(aircraftType.getAircraftUsageType())
          .asnAccidentUrl(aircraftType.getAsnAccidentUrl())

          // 상세 페이지 및 이미지 정보
          .detailsPageUrl(url)
          .mainImageUrl(mainImageUrl)
          .additionalImageUrls(additionalImageUrls)

          // Performance 데이터
          .takeOffV2(takeOffV2)
          .takeOffDistance(takeOffDistance)
          .takeOffWTC(takeOffWTC)
          .takeOffRECAT(takeOffRECAT)
          .takeOffMTOW(takeOffMTOW)

          .initialClimbIAS(initialClimbIAS)
          .initialClimbROC(initialClimbROC)

          .climb150IAS(climb150IAS)
          .climb150ROC(climb150ROC)

          .climb240IAS(climb240IAS)
          .climb240ROC(climb240ROC)

          .machClimbMACH(machClimbMACH)
          .machClimbROC(machClimbROC)

          .cruiseTAS(cruiseTAS)
          .cruiseMACH(cruiseMACH)
          .cruiseCeiling(cruiseCeiling)
          .cruiseRange(cruiseRange)

          .initialDescentMACH(initialDescentMACH)
          .initialDescentROD(initialDescentROD)

          .descentIAS(descentIAS)
          .descentROD(descentROD)

          .approachIAS(approachIAS)
          .approachROD(approachROD)
          .approachMCS(approachMCS)

          .landingVat(landingVat)
          .landingDistance(landingDistance)
          .landingAPC(landingAPC)

          // Type 정보
          .type(type)
          .typeAPC(typeAPC)
          .typeWTC(typeWTC)
          .typeRecat(typeRecat)

          // Technical 정보
          .wingSpan(wingSpan)
          .length(length)
          .height(height)
          .powerPlant(powerPlant)

          // Recognition 정보
          .wingPosition(wingPosition)
          .enginePosition(enginePosition)
          .tailConfiguration(tailConfiguration)
          .landingGear(landingGear)
          .recognitionSimilarity(recognitionSimilarity)

          // Supplementary 정보
          .iataCode(iataCode)
          .accommodation(accommodation)
          .supplementaryNotes(supplementaryNotes)
          .alternativeNames(alternativeNames)

          .build();

      // DB에 저장
      Aircraft saved = aircraftRepository.save(aircraft);
      return saved;

    } catch (IOException e) {
      log.error("Error scraping URL: {}", url, e);
      return null;
    }
  }
}