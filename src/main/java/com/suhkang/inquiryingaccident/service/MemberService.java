package com.suhkang.inquiryingaccident.service;

import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  public MyInfoResponse myInfo(Member member) {
    return MyInfoResponse.builder()
        .member(member)
        .build();
  }
}
