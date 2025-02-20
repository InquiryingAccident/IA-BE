package com.suhkang.inquiryingaccident.object.dao;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Airport extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID airportId;

  private String code;

  private String codeIcao;

  private String codeIata;

  private String codeLid;

  private String timezone;

  private String name;

  private String city;

  private String airportInfoUrl;
}
