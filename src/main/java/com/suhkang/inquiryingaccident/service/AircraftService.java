package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.dao.Aircraft;
import com.suhkang.inquiryingaccident.repository.AircraftRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AircraftService {

  private final AircraftRepository aircraftRepository;

  // 신규 항공기 정보 저장
  public Aircraft saveAircraft(Aircraft aircraft) {
    return aircraftRepository.save(aircraft);
  }

  // 항공기 ID로 조회
  public Optional<Aircraft> getAircraftById(UUID id) {
    return aircraftRepository.findById(id);
  }

  // 모든 항공기 정보 조회
  public List<Aircraft> getAllAircraft() {
    return aircraftRepository.findAll();
  }

  // 항공기 정보 업데이트
  public Aircraft updateAircraft(Aircraft aircraft) {
    return aircraftRepository.save(aircraft);
  }

  // 항공기 정보 삭제
  public void deleteAircraft(UUID id) {
    aircraftRepository.deleteById(id);
  }
}
