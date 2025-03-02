package com.suhkang.inquiryingaccident.global.init;

import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.logServerInitDuration;

import com.suhkang.inquiryingaccident.global.docs.GithubIssueService;
import com.suhkang.inquiryingaccident.service.AircraftInitializationService;
import com.suhkang.inquiryingaccident.service.AircraftTypeInitializationService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaneAccidentFinderInitiation implements ApplicationRunner {

	private final GithubIssueService githubIssueService;
	private final DatabaseInitializationService databaseInitializationService;
	private final AircraftTypeInitializationService aircraftTypeInitializationService;
	private final AircraftInitializationService aircraftInitializationService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		lineLog("SERVER START");
		lineLog("데이터 초기화 시작");
		LocalDateTime startTime = LocalDateTime.now();

		// Github 이슈 업데이트
		githubIssueService.syncGithubIssues();

		// Plane Accident DB 업데이트 (별도 관리)
		databaseInitializationService.initDatabase();

		// AircraftType 업데이트
		aircraftTypeInitializationService.initDatabase();

		// Aircraft 정보 업데이트
		aircraftInitializationService.initDatabase();

		logServerInitDuration(startTime);
		log.info("서버 데이터 초기화 및 업데이트 완료");
	}
}