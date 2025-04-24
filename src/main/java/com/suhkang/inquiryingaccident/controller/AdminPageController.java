package com.suhkang.inquiryingaccident.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.suhsaechan.suhlogger.annotation.LogMonitor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Tag(
		name = "관리자 페이지 관리",
		description = "관리자 페이지 URL -> HTML 연결"
)
public class AdminPageController implements AdminPageControllerDocs{

	@GetMapping("/")
	@LogMonitor
	public String indexPage() {
		return "admin/dashboard";
	}

	@GetMapping("/login")
	@LogMonitor
	public String loginPage() {
		return "admin/login";
	}

	@GetMapping("/dashboard")
	@LogMonitor
	public String dashboardPage(Model model){
		return "admin/dashboard";
	}
}