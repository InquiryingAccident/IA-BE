package com.suhkang.inquiryingaccident.util.filter;

import com.suhkang.inquiryingaccident.service.CustomUserDetailsService;
import com.suhkang.inquiryingaccident.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
      CustomUserDetailsService customUserDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = getTokenStrFromBearer(request);
    if (token != null && jwtTokenProvider.validateToken(token)) {
      // 토큰에서 memberId 추출
      UUID memberId = jwtTokenProvider.getMemberIdFromToken(token);
      // memberId를 기반으로 사용자 정보를 조회
      UserDetails userDetails = customUserDetailsService.loadUserByMemberId(memberId);
      if (userDetails != null) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    filterChain.doFilter(request, response);
  }

  private String getTokenStrFromBearer(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
