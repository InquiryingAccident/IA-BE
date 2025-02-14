package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.global.util.CommonUtil;
import com.suhkang.inquiryingaccident.object.dao.Accident;
import com.suhkang.inquiryingaccident.object.request.SearchAccidentInfoRequest;
import com.suhkang.inquiryingaccident.object.request.searchAccidentInfoByRegistrationRequest;
import com.suhkang.inquiryingaccident.object.response.SearchAccidentInfoResponse;
import com.suhkang.inquiryingaccident.object.response.searchAccidentInfoByRegistrationResponse;
import com.suhkang.inquiryingaccident.repository.AccidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaneAccidentService {

  private final AccidentRepository accidentRepository;

  public SearchAccidentInfoResponse searchAccidentInfo(SearchAccidentInfoRequest request) {

    // 문자열 필드는 null인 경우 빈 문자열로 대체
    // 이외: accidentDate, fatalities, aircraftRegistrationCode, hasPreliminaryReport 값 없으면 null 처리
    String wikibaseId   = CommonUtil.nvl(request.getWikibaseId());
    String aircraftType = CommonUtil.nvl(request.getAircraftType());
    String registration = CommonUtil.nvl(request.getRegistration());
    String operator     = CommonUtil.nvl(request.getOperator());
    String location     = CommonUtil.nvl(request.getLocation());
    String damage       = CommonUtil.nvl(request.getDamage());

    // 정렬 검증 및 생성
    String sortField = request.getSortField();
    if (!isSupportedSortField(sortField)) {
      sortField = "accidentDate";
    }

    String sortDirection = request.getSortDirection();
    Sort sort = "ASC".equalsIgnoreCase(sortDirection)
        ? Sort.by(sortField).ascending()
        : Sort.by(sortField).descending();

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

    Page<Accident> pageResult = accidentRepository.findByDynamicQuery(
        wikibaseId,
        request.getAccidentDate(),
        aircraftType,
        registration,
        operator,
        request.getFatalities(),
        location,
        request.getAircraftRegistrationCode(),
        damage,
        request.getHasPreliminaryReport(),
        pageable
    );

    return SearchAccidentInfoResponse.builder()
        .accidentPage(pageResult)
        .build();
  }

  /**
   * 지원하는 정렬 필드인지 검증합니다.
   *
   * 지원 필드: wikibaseId, accidentDate, aircraftType, registration, operator,
   * fatalities, location, aircraftRegistrationCode, damage, hasPreliminaryReport
   */
  private boolean isSupportedSortField(String sortField) {
    return sortField != null && (
        sortField.equals("wikibaseId") ||
            sortField.equals("accidentDate") ||
            sortField.equals("aircraftType") ||
            sortField.equals("registration") ||
            sortField.equals("operator") ||
            sortField.equals("fatalities") ||
            sortField.equals("location") ||
            sortField.equals("aircraftRegistrationCode") ||
            sortField.equals("damage") ||
            sortField.equals("hasPreliminaryReport")
    );
  }

  public searchAccidentInfoByRegistrationResponse searchAccidentInfoByRegistration(
      searchAccidentInfoByRegistrationRequest request) {

    // registration 필드에 대한 기본값 설정
    String registration = CommonUtil.nvl(request.getRegistration());

    // 정렬 필드 검증 및 생성 (지원하지 않는 필드는 기본 accidentDate로 처리)
    String sortField = request.getSortField();
    if (!isSupportedSortField(sortField)) {
      sortField = "accidentDate";
    }

    // 정렬 방향: 기본 DESC (최신순)
    String sortDirection = request.getSortDirection();
    Sort sort = "ASC".equalsIgnoreCase(sortDirection)
        ? Sort.by(sortField).ascending()
        : Sort.by(sortField).descending();

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

    // registration만 필터링
    Page<Accident> accidentPage = accidentRepository.findByDynamicQuery(
        "",                   // wikibaseId
        null,                 // accidentDate
        "",                   // aircraftType
        registration,         // registration
        "",                   // operator
        null,                 // fatalities
        "",                   // location
        null,                 // aircraftRegistrationCode
        "",                   // damage
        null,                 // hasPreliminaryReport
        pageable
    );

    return searchAccidentInfoByRegistrationResponse.builder()
        .accidentPage(accidentPage)
        .build();
  }

}
