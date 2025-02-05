package com.suhkang.inquiryingaccident.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/")
	public String indexPage() {
		return "pages/dashboard";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "pages/login";
	}

	@GetMapping("/dashboard")
	public String dashboardPage(Model model){
		return "pages/dashboard";
	}
}
