package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.docs.ApiChangeLog;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLogs;
import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.AircraftInfoByModelCodeRequest;
import com.suhkang.inquiryingaccident.object.request.FlightInfoByFlightAwareApiRequest;
import com.suhkang.inquiryingaccident.object.response.FlightInfoByFlightAwareApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface FlightInfoControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.03.02",
          author = Author.SUHSAECHAN,
          issueNumber = 23,
          description = "FlightAware API 연동 및 항공편 정보 조회 기능 구현"
      ),
  })
  @Operation(
      summary = "항공편 정보를 조회",
      description = """
      ## 인증(JWT): **필요**
      
      ## 요청 파라미터 (FlightInfoByFlightAwareApiRequest)
      - **`flightNumber`**: 항공편 번호 (예: AA123)

      ## 반환값 (FlightInfoByFlightAwareApiResponse)
      - **`flights`**: 항공편 정보 목록
        - **`ident`**: 항공편 식별자 (예: "AA123")
        - **`identIcao`**: ICAO 코드 기반 식별자
        - **`identIata`**: IATA 코드 기반 식별자
        - **`actualRunwayOff`**: 실제 이륙 시간
        - **`actualRunwayOn`**: 실제 착륙 시간
        - **`faFlightId`**: FlightAware에서 제공하는 고유 항공편 ID
        - **`operator`**: 항공사 코드 또는 이름
        - **`operatorIcao`**: ICAO 기반 항공사 코드
        - **`operatorIata`**: IATA 기반 항공사 코드
        - **`flightNumber`**: 항공편 번호
        - **`registration`**: 항공기 등록 번호
        - **`atcIdent`**: ATC (항공교통관제) 식별자
        - **`inboundFaFlightId`**: 이전 항공편의 FlightAware ID
        - **`codeshares`**: 코드셰어 항공편 목록
        - **`codesharesIata`**: IATA 기반 코드셰어 항공편 목록
        - **`blocked`**: 항공편 정보 차단 여부
        - **`diverted`**: 회항 여부
        - **`cancelled`**: 취소 여부
        - **`positionOnly`**: 위치 정보만 제공하는 항공편 여부
        - **`origin`**: 출발 공항 정보
          - **`code`**: 공항 코드
          - **`codeIcao`**: ICAO 공항 코드
          - **`codeIata`**: IATA 공항 코드
          - **`timezone`**: 공항 시간대
          - **`name`**: 공항 이름
          - **`city`**: 공항 위치한 도시
          - **`airportInfoUrl`**: 공항 정보 URL
        - **`destination`**: 도착 공항 정보 (출발 공항과 동일한 구조)
        - **`departureDelay`**: 출발 지연 시간 (초)
        - **`arrivalDelay`**: 도착 지연 시간 (초)
        - **`filedEte`**: 예정 비행 시간 (초)
        - **`foresightPredictionsAvailable`**: 예측 데이터 제공 여부
        - **`scheduledOut`**: 예정된 탑승교 출발 시간
        - **`estimatedOut`**: 예상된 탑승교 출발 시간
        - **`actualOut`**: 실제 탑승교 출발 시간
        - **`scheduledOff`**: 예정된 이륙 시간
        - **`estimatedOff`**: 예상된 이륙 시간
        - **`actualOff`**: 실제 이륙 시간
        - **`scheduledOn`**: 예정된 착륙 시간
        - **`estimatedOn`**: 예상된 착륙 시간
        - **`actualOn`**: 실제 착륙 시간
        - **`scheduledIn`**: 예정된 도착 시간
        - **`estimatedIn`**: 예상된 도착 시간
        - **`actualIn`**: 실제 도착 시간
        - **`progressPercent`**: 비행 진행률 (0~100)
        - **`status`**: 항공편 상태 (예: "En Route", "Arrived")
        - **`aircraftType`**: 사용된 항공기 모델 코드
        - **`routeDistance`**: 총 비행 거리 (해리)
        - **`filedAirspeed`**: 제출된 비행 속도 (노트)
        - **`filedAltitude`**: 제출된 비행 고도 (피트)
        - **`route`**: 비행 경로 정보
        - **`baggageClaim`**: 수하물 찾는 곳 정보
        - **`seatsCabinBusiness`**: 비즈니스석 좌석 수
        - **`seatsCabinCoach`**: 이코노미석 좌석 수
        - **`seatsCabinFirst`**: 퍼스트 클래스 좌석 수
        - **`gateOrigin`**: 출발 게이트 번호
        - **`gateDestination`**: 도착 게이트 번호
        - **`terminalOrigin`**: 출발 터미널 번호
        - **`terminalDestination`**: 도착 터미널 번호
        - **`type`**: 항공편 유형 (예: "Scheduled", "Charter")

      - **`numPages`**: 총 페이지 수 (데이터가 여러 페이지로 제공될 경우)

      ## 에러코드
      - **`FLIGHT_NOT_FOUND`**: 해당 항공편 정보를 찾을 수 없습니다.
      """
  )

  public ResponseEntity<FlightInfoByFlightAwareApiResponse> getFlightInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute FlightInfoByFlightAwareApiRequest request);



  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.03.02",
          author = Author.SUHSAECHAN,
          issueNumber = 23,
          description = "항공기 정보 조회 API 구현"
      ),
  })
  @Operation(
      summary = "항공기 코드로 항공기 정보 조회",
      description = """
          ## 인증(JWT): **필요**
                
          ## 요청 파라미터 (AircraftInfoByModelCodeRequest)
          - **`modelCode`**: 항공기 모델 코드 (예: A319, B737)

          ## 반환값 (Aircraft)
          - **`aircraftId`**: 항공기 ID (UUID)
          - **`modelCode`**: 모델 코드
          - **`manufacturer`**: 제조사
          - **`modelName`**: 모델명
          - **`firstFlightYear`**: 첫 비행 연도
          - **`description`**: 항공기 설명
          - **`aircraftUsageType`**: 항공기 사용 유형
          - **`detailsPageUrl`**: 상세 정보 페이지 URL
          - **`mainImageUrl`**: 대표 이미지 URL
          - **`takeOffDistance`**: 이륙 거리 (m)
          - **`cruiseMACH`**: 순항 속도 (마하)
          - **`cruiseCeiling`**: 순항 고도 (FL)

          ## 에러코드
          - **`AIRCRAFT_NOT_FOUND`**: 항공기 정보를 찾을 수 없습니다.
          """
  )
  public ResponseEntity<Aircraft> getAircraftTypeInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute @Valid AircraftInfoByModelCodeRequest request);

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.03.03",
          author = Author.SUHSAECHAN,
          issueNumber = 21,
          description = "항공기 전체 모델 코드 목록 조회 API 추가"
      )
  })
  @Operation(
      summary = "모든 항공기 모델 코드 목록 조회",
      description = """
            ## 인증(JWT): **필요**
            
            ## 요청 파라미터
            - 없음
            
            ## 문제점
            - 항공기 정보를 DB에 가지고 있으나, 프론트가 필요로 하는 정보가 불확실함.
            - 프론트엔드가 전체 항공기 리스트를 필요로 할 가능성 있음.
            
            ## 해결 방안 / 제안 기능
            - DB에서 고유한 항공기 `modelCode` 목록을 반환하는 API 구현.
            
            ## 반환값
            - **`aircraftModelCodes`**: 고유한 모델 코드 목록 (예: ["A319", "B737", "B763"])
            
            """
  )
  ResponseEntity<List<String>> getAllAircraftModelCodes(
      @AuthenticationPrincipal CustomUserDetails customUserDetails);
}
