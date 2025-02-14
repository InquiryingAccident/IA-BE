package com.suhkang.inquiryingaccident.global.init;

import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.logServerInitDuration;
import com.suhkang.inquiryingaccident.global.docs.GithubIssueService;
import com.suhkang.inquiryingaccident.service.PlaneAccidentService;
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
	private final PlaneAccidentService planeAccidentService;
	private final DatabaseInitializationService databaseInitializationService;

	@Override
	// 모든 Bean 등록 완료시 실행
	public void run(ApplicationArguments args) throws Exception {
		lineLog("SERVER START");
		lineLog("데이터 초기화 시작");
		LocalDateTime startTime = LocalDateTime.now();

		// Github 이슈 업데이트
		githubIssueService.syncGithubIssues();

		// Plane Accident DB 업데이트 (한번만 수행)
		databaseInitializationService.initializeOrUpdateDatabase();

		logServerInitDuration(startTime);
		log.info("서버 데이터 초기화 및 업데이트 완료");
	}
}
