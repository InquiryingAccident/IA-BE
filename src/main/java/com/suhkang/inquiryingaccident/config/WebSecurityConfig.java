package com.suhkang.inquiryingaccident.config;

import com.suhkang.inquiryingaccident.service.CustomUserDetailsService;
import com.suhkang.inquiryingaccident.global.util.JwtTokenProvider;
import com.suhkang.inquiryingaccident.global.filter.CustomAuthenticationEntryPoint;
import com.suhkang.inquiryingaccident.global.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;

  public WebSecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.customUserDetailsService = customUserDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
        .authorizeHttpRequests(auth -> auth
            // 허용 URL
            .requestMatchers(SecurityUrls.AUTH_WHITELIST.toArray(new String[0])).permitAll()
            // 관리자 URL
            .requestMatchers(SecurityUrls.ADMIN_PATHS.toArray(new String[0])).hasRole("ADMIN")
            // 회원 관련 예시 URL
            .requestMatchers(HttpMethod.POST, "/api/member/my-page").hasAnyRole("ADMIN", "USER")
            .anyRequest().authenticated()
        );

    // JWT 인증 필터 추가
    http.addFilterBefore(
        new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
        UsernamePasswordAuthenticationFilter.class
    );

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
