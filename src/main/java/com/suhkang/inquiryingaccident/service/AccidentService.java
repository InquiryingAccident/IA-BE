package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.repository.AccidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccidentService {
  private final AccidentRepository accidentRepository;
}
