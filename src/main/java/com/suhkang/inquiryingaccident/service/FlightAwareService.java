package com.suhkang.inquiryingaccident.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suhkang.inquiryingaccident.object.request.FlightInfoByFlightAwareApiRequest;
import com.suhkang.inquiryingaccident.object.response.FlightInfoByFlightAwareApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightAwareService {

  private final OkHttpClient okHttpClient;
  private final ObjectMapper objectMapper;

  @Value("${flightaware.api-key}")
  private String flightAwareApiKey;

  @Value("${flightaware.base-url}")
  private String flightAwareBaseUrl;

  public FlightInfoByFlightAwareApiResponse fetchFlightInfo(FlightInfoByFlightAwareApiRequest request) {
    String flightNumber = request.getFlightNumber();
    String url = flightAwareBaseUrl + "/flights/" + flightNumber;
    log.info("FlightAware API 호출: {}", url);

    Request apiRequest = new Request.Builder()
        .url(url)
        .header("x-apikey", flightAwareApiKey)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        .header("Accept", "application/json")
        .build();

    try (Response response = okHttpClient.newCall(apiRequest).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        log.error("FlightAware API 호출 실패: {} - 응답 코드: {}", url, response.code());
        return new FlightInfoByFlightAwareApiResponse();
      }
      String responseBody = response.body().string();
      log.info("API 응답 본문: {}", responseBody);
      return objectMapper.readValue(responseBody, FlightInfoByFlightAwareApiResponse.class);
    } catch (IOException ex) {
      log.error("FlightAware API 호출 중 오류 발생: {}", ex.getMessage(), ex);
      return new FlightInfoByFlightAwareApiResponse();
    }
  }
}