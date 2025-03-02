package com.suhkang.inquiryingaccident.object.dao;

import com.suhkang.inquiryingaccident.object.constants.AircraftUsageType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Aircraft{

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID aircraftId;

  // 기본 항공기 정보
  @Column(length = 10)
  private String modelCode;           // 예: "A318"
  private String manufacturer;        // 예: "AIRBUS"
  private String modelName;           // 예: "A318"
  private Integer firstFlightYear;    // 예: 1997
  @Column(length = 1500)
  private String description;         // 예: "twin-jet airliner" 등
  @Enumerated(EnumType.STRING)
  private AircraftUsageType aircraftUsageType;
  private String asnAccidentUrl;      // ASN 사고 내역 URL

  // 상세 페이지 및 이미지 URL 정보
  private String detailsPageUrl;      // 예: "https://contentzone.eurocontrol.int/aircraftperformance/details.aspx?ICAO=A320&"
  private String mainImageUrl;        // MainContent_wsDrawing 의 src 값
  @ElementCollection
  private List<String> additionalImageUrls; // detailShowAllPhotos 등 나머지 이미지 URL 목록

  // Take-off 성능 데이터
  private Integer takeOffV2;          // kts, 예: 135
  private Integer takeOffDistance;    // m, 예: 1400
  private String takeOffWTC;          // 예: "M"
  private String takeOffRECAT;        // 예: "Upper Medium"
  private Integer takeOffMTOW;        // kg, 예: 59000

  // Initial climb (to 5000ft)
  private Integer initialClimbIAS;    // kts, 예: 165
  private Integer initialClimbROC;    // ft/min, 예: 3000

  // Climb (to FL 150)
  private Integer climb150IAS;        // kts, 예: 290
  private Integer climb150ROC;        // ft/min, 예: 2000

  // Climb (to FL 240)
  private Integer climb240IAS;        // kts, 예: 290
  private Integer climb240ROC;        // ft/min, 예: 2000

  // MACH climb
  private Double machClimbMACH;       // 예: 0.76
  private Integer machClimbROC;       // ft/min, 예: 1000

  // Cruise
  private Integer cruiseTAS;          // kts, 예: 460
  private Double cruiseMACH;          // 예: 0.79
  private Integer cruiseCeiling;      // FL, 예: 390
  private Integer cruiseRange;        // NM, 예: 1500

  // Initial Descent (to FL 240)
  private Double initialDescentMACH;  // 예: 0.76
  private Integer initialDescentROD;  // ft/min, 예: 800

  // Descent (to FL 100)
  private Integer descentIAS;         // kts, 예: 290
  private Integer descentROD;         // ft/min, 예: 3500

  // Approach
  private Integer approachIAS;        // kts, 예: 250
  private Integer approachROD;        // ft/min, 예: 1500
  private Integer approachMCS;        // kts, 예: 210

  // Landing
  private Integer landingVat;         // Vat (IAS) in kts, 예: 125
  private Integer landingDistance;    // m, 예: 1300
  private String landingAPC;          // 예: "C"

  // Type of Aircraft (추가 타입 정보)
  private String type;                // 예: "L2J"
  private String typeAPC;             // 예: "C"
  private String typeWTC;             // 예: "M"
  private String typeRecat;           // 예: "Upper Medium"

  // Technical 정보
  private Double wingSpan;            // m, 예: 34.1
  private Double length;              // m, 예: 31.45
  private Double height;              // m, 예: 12.56
  @Column(length = 1500)
  private String powerPlant;          // 예: "2 x 90kN P&W PW6124 or 2 x 98kN CFM 56-5B turbofans."

  // Recognition 정보
  private String wingPosition;        // 예: "Low wing"
  private String enginePosition;      // 예: "Underwing mounted"
  private String tailConfiguration;   // 예: "Regular tail, mid set"
  private String landingGear;         // 예: "Tricycle retractable"
  private String recognitionSimilarity; // 예: "ICAO Code: A319; The A-318 has the shortest fuselage in the A-320 family."

  // Supplementary 정보
  private String iataCode;            // 예: "318 / 32S"
  private String accommodation;       // 예: "Flightcrew of two and typical 107 passengers..."
  @Column(length = 1500)
  private String supplementaryNotes;  // 예: "Short range, mid-size single aisle airliner..."
  @ElementCollection
  private List<String> alternativeNames; // 대체 명칭 목록 (중요하지 않으면 빈 리스트)

}
