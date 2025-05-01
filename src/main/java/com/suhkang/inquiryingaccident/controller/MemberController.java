package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.global.util.CommonUtil;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.IsEmailAvailableRequest;
import com.suhkang.inquiryingaccident.object.response.MyInfoResponse;
import com.suhkang.inquiryingaccident.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.suhsaechan.suhlogger.annotation.LogMonitor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Tag(
    name = "회원 관리 API",
    description = "회원 관리 API 제공"
)
public class MemberController implements MemberControllerDocs {

  private final MemberService memberService;

  @PostMapping("/my-info")
  @LogMonitor
  public ResponseEntity<MyInfoResponse> myInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    return ResponseEntity.ok(memberService.myInfo(customUserDetails.getMember()));
  }

  @DeleteMapping("/withdraw")
  @LogMonitor
  public ResponseEntity<Void> withdraw(
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    memberService.withdraw(customUserDetails.getMember().getMemberId());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping(value = "/check-email", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitor
  public ResponseEntity<Boolean> isEmailAvailable(
      @ModelAttribute IsEmailAvailableRequest request) {
    return ResponseEntity.ok(memberService.isEmailAvailable(request));
  }

  @PostMapping(value = "/random-nickname")
  @LogMonitor
  public ResponseEntity<String> getRandomNickname(){
    return ResponseEntity.ok(CommonUtil.getRandomNickname());
  }

  @PostMapping(value = "/random-mature-nickname")
  @LogMonitor
  public ResponseEntity<String> getRandomMatureNickname(){
    return ResponseEntity.ok(CommonUtil.getRandomMatureNickname());
  }

  @PostMapping(value = "/random-politician-nickname")
  @LogMonitor
  public ResponseEntity<String> getRandomPoliticianNickname(){
    return ResponseEntity.ok(CommonUtil.getRandomPoliticianNickname());
  }

}
