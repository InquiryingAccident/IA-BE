package com.suhkang.inquiryingaccident.object.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suhkang.inquiryingaccident.object.constants.AccountStatus;
import com.suhkang.inquiryingaccident.object.constants.Role;
import com.suhkang.inquiryingaccident.object.constants.SocialPlatform;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SoftDelete(columnName = "is_deleted")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID memberId;

  private String email;

  // 소셜로그인이 아닌경우 존재
  private String password;

  @Enumerated(EnumType.STRING)
  private SocialPlatform socialPlatform;

  // 애플 소셜로그인 때 확인 필요
  private String socialPlatformId;

  private String nickname;

  @Column
  private String profileUrl;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
  @Column(name = "role")
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus = AccountStatus.ACTIVE;

  private LocalDateTime lastLoginTime;

  @Builder.Default
  @JsonIgnore
  private Boolean isFirstLogin = true;
}
