package com.suhkang.inquiryingaccident.service;

import static com.suhkang.inquiryingaccident.global.util.LogUtil.lineLog;
import static com.suhkang.inquiryingaccident.global.util.LogUtil.superLogDebug;

import com.suhkang.inquiryingaccident.global.exception.CustomException;
import com.suhkang.inquiryingaccident.global.exception.ErrorCode;
import com.suhkang.inquiryingaccident.global.util.JwtTokenProvider;
import com.suhkang.inquiryingaccident.object.constants.AccountStatus;
import com.suhkang.inquiryingaccident.object.constants.JwtTokenType;
import com.suhkang.inquiryingaccident.object.constants.Role;
import com.suhkang.inquiryingaccident.object.constants.SocialPlatform;
import com.suhkang.inquiryingaccident.object.dao.Member;
import com.suhkang.inquiryingaccident.object.dao.RefreshToken;
import com.suhkang.inquiryingaccident.object.dto.CustomUserDetails;
import com.suhkang.inquiryingaccident.object.request.LoginRequest;
import com.suhkang.inquiryingaccident.object.request.LogoutRequest;
import com.suhkang.inquiryingaccident.object.request.RefreshAccessTokenByRefreshTokenRequest;
import com.suhkang.inquiryingaccident.object.request.SignupRequest;
import com.suhkang.inquiryingaccident.object.request.SocialLoginRequest;
import com.suhkang.inquiryingaccident.object.response.LoginResponse;
import com.suhkang.inquiryingaccident.object.response.RefreshAccessTokenByRefreshTokenResponse;
import com.suhkang.inquiryingaccident.object.response.SignUpResponse;
import com.suhkang.inquiryingaccident.object.response.SocialLoginResponse;
import com.suhkang.inquiryingaccident.repository.MemberRepository;
import com.suhkang.inquiryingaccident.repository.RefreshTokenRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.suhnicknamegenerator.core.SuhRandomKit;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  SuhRandomKit suhRandomKit = SuhRandomKit.builder()
      .locale("ko")
      .numberLength(4)
      .uuidLength(4)
      .enableAdultContent(true)
      .build();


  public SignUpResponse signup(SignupRequest request) {
    // 기존 코드 유지
    log.info("회원가입 요청 시작 - email: {}", request.getEmail());
    lineLog("Checking email duplication");

    // 활성 회원 중 email 중복 체크
    if (memberRepository.existsByEmail(request.getEmail())) {
      log.warn("중복된 이메일 감지 - email: {}", request.getEmail());
      throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
    }
    log.debug("이메일 중복 없음 - email: {}", request.getEmail());

    lineLog("Creating new member");
    Member member = Member.builder()
        .email(request.getEmail())
        .nickname(request.getNickname())
        .password(passwordEncoder.encode(request.getPassword()))
        .roles(Set.of(Role.ROLE_USER))
        .accountStatus(AccountStatus.ACTIVE)
        .build();

    Member savedMember = memberRepository.save(member);
    superLogDebug(savedMember); // 저장된 회원 객체 출력
    log.info("회원가입 완료 - memberId: {}", savedMember.getMemberId());

    return SignUpResponse.builder()
        .member(savedMember)
        .build();
  }

  public LoginResponse login(LoginRequest request) {
    // 기존 코드 유지
    log.info("로그인 요청 시작 - email: {}", request.getEmail());
    lineLog("Authenticating user");

    // 인증 처리
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.debug("인증 성공 - email: {}", request.getEmail());

    String accessToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.ACCESS);
    String refreshToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.REFRESH);
    log.debug("토큰 생성 완료 - accessToken: {}, refreshToken: {}", accessToken, refreshToken);

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Member member = userDetails.getMember();

    lineLog("Updating last login time");
    member.setLastLoginTime(LocalDateTime.now());
    memberRepository.save(member);
    log.debug("마지막 로그인 시간 업데이트 - memberId: {}", member.getMemberId());

    lineLog("Saving refresh token");
    RefreshToken refreshTokenEntity = RefreshToken.builder()
        .token(refreshToken)
        .memberId(member.getMemberId())
        .memberEmail(member.getEmail())
        .expiryDate(Instant.now().plusMillis(JwtTokenType.REFRESH.getDurationMilliseconds()))
        .build();
    refreshTokenRepository.save(refreshTokenEntity);
    superLogDebug(refreshTokenEntity); // 리프레시 토큰 객체 출력

    log.info("로그인 완료 - memberId: {}", member.getMemberId());
    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Transactional
  public SocialLoginResponse SocialLogin(SocialLoginRequest request) {
    log.info("소셜 로그인 요청 시작 - email: {}, socialPlatform: {}", request.getEmail(), request.getSocialPlatform());
    lineLog("Validating request parameters");

    // 소셜 플랫폼 검증
    if (request.getSocialPlatform() == null) {
      log.warn("소셜 플랫폼이 누락되었습니다.");
      throw new CustomException(ErrorCode.INVALID_SOCIAL_PLATFORM);
    }

    // 애플 로그인 특수 처리 - socialPlatformId는 필수
    if (SocialPlatform.APPLE.equals(request.getSocialPlatform())) {
      if (request.getSocialPlatformId() == null || request.getSocialPlatformId().trim().isEmpty()) {
        log.warn("애플 로그인 요청에 socialPlatformId가 누락되었습니다.");
        throw new CustomException(ErrorCode.MISSING_SOCIAL_PLATFORM_ID);
      }
    }
    // 애플이 아닌 다른 소셜 로그인의 경우 이메일은 항상 필수
    else if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
      log.warn("소셜 로그인 요청에 이메일이 누락되었습니다.");
      throw new CustomException(ErrorCode.MISSING_EMAIL);
    }

    lineLog("Checking if member exists");
    Member member = null;
    boolean isFirstLogin = false;

    // 애플 로그인의 경우
    if (SocialPlatform.APPLE.equals(request.getSocialPlatform())) {
      log.debug("애플 로그인 처리 - socialPlatformId: {}", request.getSocialPlatformId());

      // 1. socialPlatformId로 먼저 회원 검색 (이후 로그인의 경우)
      Optional<Member> memberByAppleId = memberRepository.findBySocialPlatformAndSocialPlatformId(
          SocialPlatform.APPLE, request.getSocialPlatformId());

      if (memberByAppleId.isPresent()) {
        member = memberByAppleId.get();
        log.debug("애플 ID로 회원 찾음 - memberId: {}", member.getMemberId());
      }
      // 2. 애플 ID로 회원을 찾지 못했고, 이메일이 제공된 경우 (첫 로그인)
      else if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
        Optional<Member> memberByEmail = memberRepository.findByEmailAndSocialPlatform(
            request.getEmail(), SocialPlatform.APPLE);

        if (memberByEmail.isPresent()) {
          member = memberByEmail.get();
          log.debug("이메일과 애플 플랫폼으로 회원 찾음 - memberId: {}", member.getMemberId());

          // ID 정보 없으면 갱신
          if (member.getSocialPlatformId() == null || member.getSocialPlatformId().trim().isEmpty()) {
            member.setSocialPlatformId(request.getSocialPlatformId());
            member = memberRepository.save(member);
            log.debug("애플 ID 업데이트 - memberId: {}, appleId: {}", member.getMemberId(), request.getSocialPlatformId());
          }
        }
      }
    }
    // 다른 소셜 로그인의 경우 (항상 이메일 필요)
    else {
      Optional<Member> memberByEmail = memberRepository.findByEmailAndSocialPlatform(
          request.getEmail(), request.getSocialPlatform());

      if (memberByEmail.isPresent()) {
        member = memberByEmail.get();
        log.debug("이메일과 소셜 플랫폼으로 회원 찾음 - memberId: {}", member.getMemberId());
      }
    }

    // 회원이 존재하지 않는 경우 신규 가입
    if (member == null) {
      // 애플 첫 로그인이면서 이메일이 없는 경우 - 에러
      if (SocialPlatform.APPLE.equals(request.getSocialPlatform())
          && (request.getEmail() == null || request.getEmail().trim().isEmpty())) {
        log.warn("애플 첫 로그인 시 이메일 정보가 필요합니다.");
        throw new CustomException(ErrorCode.MISSING_EMAIL);
      }

      isFirstLogin = true;
      log.debug("신규 소셜 회원 가입 - email: {}, socialPlatform: {}", request.getEmail(), request.getSocialPlatform());

      member = Member.builder()
          .email(request.getEmail())
          .socialPlatform(request.getSocialPlatform())
          .socialPlatformId(request.getSocialPlatformId())
          .nickname(suhRandomKit.politicianNickname())
          .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
          .accountStatus(AccountStatus.ACTIVE)
          .isFirstLogin(true)
          .build();

      member = memberRepository.save(member);
      superLogDebug(member);
      log.info("신규 소셜 회원 가입 완료 - memberId: {}", member.getMemberId());
    }

    // 로그인 처리
    CustomUserDetails userDetails = new CustomUserDetails(member);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    lineLog("Generating tokens");
    String accessToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.ACCESS);
    String refreshToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.REFRESH);
    log.debug("토큰 생성 완료 - accessToken: {}, refreshToken: {}", accessToken, refreshToken);

    // 마지막 로그인 시간 업데이트
    member.setLastLoginTime(LocalDateTime.now());

    // 첫 로그인이 아닌 경우 isFirstLogin 속성 업데이트
    if (!isFirstLogin && member.getIsFirstLogin()) {
      member.setIsFirstLogin(false);
    }

    memberRepository.save(member);

    // 리프레시 토큰 저장
    RefreshToken refreshTokenEntity = RefreshToken.builder()
        .token(refreshToken)
        .memberId(member.getMemberId())
        .memberEmail(member.getEmail())
        .expiryDate(Instant.now().plusMillis(JwtTokenType.REFRESH.getDurationMilliseconds()))
        .build();
    refreshTokenRepository.save(refreshTokenEntity);

    log.info("소셜 로그인 완료 - memberId: {}, isFirstLogin: {}", member.getMemberId(), isFirstLogin);
    return SocialLoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .isFirstLogin(isFirstLogin)
        .build();
  }

  public RefreshAccessTokenByRefreshTokenResponse refreshAccessTokenByRefreshToken(
      RefreshAccessTokenByRefreshTokenRequest request) {
    // 기존 코드 유지
    log.info("토큰 갱신 요청 시작 - refreshToken: {}", request.getRefreshToken());
    lineLog("Finding refresh token");

    RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> {
          log.warn("유효하지 않은 리프레시 토큰 - token: {}", request.getRefreshToken());
          return new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        });
    superLogDebug(refreshTokenEntity); // 리프레시 토큰 객체 출력

    // 토큰 상태 확인
    if (RefreshToken.isExpired(refreshTokenEntity)) {
      log.info("만료된 토큰 감지 - memberEmail: {}", refreshTokenEntity.getMemberEmail());
    } else {
      log.info("유효한 토큰 확인 - memberEmail: {}", refreshTokenEntity.getMemberEmail());
    }

    lineLog("Retrieving member");
    Member member = memberRepository.findById(refreshTokenEntity.getMemberId())
        .orElseThrow(() -> {
          log.warn("회원 조회 실패 - memberId: {}", refreshTokenEntity.getMemberId());
          return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        });
    CustomUserDetails userDetails = new CustomUserDetails(member);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    lineLog("Generating new tokens");
    String newAccessToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.ACCESS);
    String newRefreshToken = jwtTokenProvider.generateToken(authentication, JwtTokenType.REFRESH);
    log.debug("새 토큰 생성 - newAccessToken: {}, newRefreshToken: {}", newAccessToken, newRefreshToken);

    lineLog("Updating refresh token");
    refreshTokenEntity.setToken(newRefreshToken);
    refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(JwtTokenType.REFRESH.getDurationMilliseconds()));
    refreshTokenRepository.save(refreshTokenEntity);
    superLogDebug(refreshTokenEntity); // 업데이트된 리프레시 토큰 출력

    log.info("토큰 갱신 완료 - memberEmail: {}", member.getEmail());
    return RefreshAccessTokenByRefreshTokenResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }

  @Transactional
  public void logout(LogoutRequest request) {
    // 기존 코드 유지
    log.info("로그아웃 요청 시작 - memberEmail: {}", request.getMember().getEmail());
    lineLog("Finding refresh token");

    RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> {
          log.warn("리프레시 토큰 조회 실패 - token: {}", request.getRefreshToken());
          return new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        });
    superLogDebug(refreshToken); // 리프레시 토큰 객체 출력

    lineLog("Deleting refresh token");
    refreshTokenRepository.deleteById(refreshToken.getRefreshTokenId());
    log.debug("리프레시 토큰 삭제 완료 - refreshTokenId: {}", refreshToken.getRefreshTokenId());

    lineLog("Logout completed");
    log.info("회원 로그아웃 완료 - email: {}", request.getMember().getEmail());
  }
}