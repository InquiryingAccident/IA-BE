package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.docs.ApiChangeLog;
import com.suhkang.inquiryingaccident.global.docs.ApiChangeLogs;
import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.SearchAccidentInfoRequest;
import com.suhkang.inquiryingaccident.object.response.SearchAccidentInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface PlaneAccidentControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.13",
          author = Author.SUHSAECHAN,
          issueNumber = 9,
          description = "비행기 사고 정보 조회 기능 구현"
      )
  })
  @Operation(
      summary = "비행기 사고 정보 조회",
      description = """
      ## 인증(JWT): **필요**
      
      ## 참고사항
      - **`wikibaseId`**: 사고를 식별하는 위키베이스 ID (부분 검색 가능)
      - **`accidentDate`**: 사고 발생 날짜 (YYYY-MM-DD 형식)
      - **`aircraftType`**: 항공기 종류 (부분 검색 가능)
      - **`registration`**: 항공기 등록 번호 (부분 검색 가능)
      - **`operator`**: 항공사명 (부분 검색 가능)
      - **`fatalities`**: 사망자 수 (정확한 숫자로 검색)
      - **`location`**: 사고 발생 지역 (부분 검색 가능)
      - **`aircraftRegistrationCode`**: 항공기 등록 코드 (정확한 코드 입력 필요)
      - **`damage`**: 피해 수준 (부분 검색 가능)
      - **`hasPreliminaryReport`**: 예비 보고서 존재 여부 (true/false)
      - **`sortField`**: 정렬 기준 필드 (지원 필드: `wikibaseId`, `accidentDate`, `aircraftType`, `registration`, `operator`, `fatalities`, `location`, `aircraftRegistrationCode`, `damage`, `hasPreliminaryReport`)
      - **`sortDirection`**: 정렬 방향 (`ASC` 또는 `DESC`)

      ## 요청 파라미터 (SearchAccidentInfoRequest)
      - **`wikibaseId`**: 위키베이스 ID (선택)
      - **`accidentDate`**: 사고 발생 날짜 (선택, `YYYY-MM-DD` 형식)
      - **`aircraftType`**: 항공기 종류 (선택)
      - **`registration`**: 항공기 등록 번호 (선택)
      - **`operator`**: 항공사명 (선택)
      - **`fatalities`**: 사망자 수 (선택)
      - **`location`**: 사고 발생 지역 (선택)
      - **`aircraftRegistrationCode`**: 항공기 등록 코드 (선택)
      - **`damage`**: 피해 수준 (선택)
      - **`hasPreliminaryReport`**: 예비 보고서 존재 여부 (선택)
      - **`sortField`**: 정렬 기준 필드 (선택, 기본값: `accidentDate`)
      - **`sortDirection`**: 정렬 방향 (선택, 기본값: `DESC`)
      - **`page`**: 페이지 번호 (0부터 시작, 기본값: 0)
      - **`size`**: 한 페이지 당 데이터 개수 (기본값: 10)

      ## 반환값 (SearchAccidentInfoResponse)
      - **`accidentPage`**: 조회된 사고 목록 (페이지네이션 적용)

      ## 에러코드
      - **`INVALID_SORT_FIELD`**: 지원되지 않는 정렬 필드를 사용하였습니다.
      - **`ACCIDENT_NOT_FOUND`**: 조회된 사고 정보가 없습니다.
      """
  )

  public ResponseEntity<SearchAccidentInfoResponse> searchAccidentInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @ModelAttribute SearchAccidentInfoRequest request);
}
