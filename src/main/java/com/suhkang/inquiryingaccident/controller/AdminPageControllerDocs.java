package com.suhkang.inquiryingaccident.controller;

import com.suhkang.inquiryingaccident.object.constants.Author;
import com.suhkang.inquiryingaccident.util.log.ApiChangeLog;
import com.suhkang.inquiryingaccident.util.log.ApiChangeLogs;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.ui.Model;

public interface AdminPageControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "관리자 페이지 대시보드 연결 구현"
      )
  })
  @Operation(
      summary = "관리자 대시보드 페이지",
      description = ""
  )
  String indexPage();

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "관리자 로그인 페이지 연결 구현"
      )
  })
  @Operation(
      summary = "관리자 로그인 페이지",
      description = ""
  )
  String loginPage();

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025.02.09",
          author = Author.SUHSAECHAN,
          issueNumber = 4,
          description = "관리자 대시보드 페이지 연결 구현"
      )
  })
  @Operation(
      summary = "관리자 대시보드 페이지",
      description = ""
  )
  String dashboardPage(Model model);
}
