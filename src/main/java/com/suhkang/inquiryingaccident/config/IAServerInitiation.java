package com.suhkang.inquiryingaccident.config;


import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IAServerInitiation implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		lineLog("서버 시작");
	}
}
