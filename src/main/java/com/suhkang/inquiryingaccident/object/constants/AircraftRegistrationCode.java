package com.suhkang.inquiryingaccident.object.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 항공기 등록코드와 대응하는 국가를 나타내는 Enum
 */
public enum AircraftRegistrationCode {

  AFGHANISTAN("YA"),
  ALBANIA("ZA"),
  ALGERIA("7T"),
  AMERICAN_SAMOA("NAS"),
  ANDORRA("C3"),
  ANGOLA("D2"),
  ANGUILLA("VP-A"),
  ANTIGUA_AND_BARBUDA("V2"),
  ARGENTINA("LV"),
  ARMENIA("EK"),
  ARUBA("P4"),
  AUSTRALIA("VH"),
  AUSTRIA("OE"),
  AZERBAIJAN("4K"),
  BAHAMAS("C6"),
  BAHRAIN("A9C"),
  BANGLADESH("S2"),
  BARBADOS("8P"),
  BELARUS("EW"),
  BELGIUM("OO"),
  BELIZE("V3"),
  BENIN("TY"),
  BERMUDA("VP-B"),
  BHUTAN("A5"),
  BOLIVIA("CP"),
  BOSNIA_AND_HERZEGOVINA("T9"),
  BOTSWANA("A2"),
  BRAZIL("PP"),
  BRITISH_VIRGIN_ISLANDS("VP-L"),
  BRUNEI("V8"),
  BULGARIA("LZ"),
  BURKINA_FASO("XT"),
  BURUNDI("9U"),
  CAMBODIA("XU"),
  CAMEROON("TJ"),
  CANADA("C"),
  CAPE_VERDE("D4"),
  CARIBBEAN_NETHERLANDS("PJbes"),
  CAYMAN_ISLANDS("VP-C"),
  CENTRAL_AFRICAN_REPUBLIC("TL"),
  CHAD("TT"),
  CHILE("CC"),
  CHINA("B"),
  COLOMBIA("HK"),
  COMOROS("D6"),
  CONGO("TN"),
  CONGO_FORMER_ZAIRE("9Q"),
  COSTA_RICA("TI"),
  COTE_DI_VOIRE("TU"),
  CROATIA("9A"),
  CUBA("CU"),
  CURACAO("PJ"),
  CYPRUS("5B"),
  CZECH_REPUBLIC("OK"),
  DENMARK("OY"),
  DJIBOUTI("J2"),
  DOMINICA("J7"),
  DOMINICAN_REPUBLIC("HI"),
  EAST_TIMOR("CR-T"),
  ECUADOR("HC"),
  EGYPT("SU"),
  EL_SALVADOR("YS"),
  EQUATORIAL_GUINEA("3C"),
  ERITREA("E3"),
  ESTONIA("ES"),
  ESWATINI("3D"),
  ETHIOPIA("ET"),
  FALKLAND_ISLANDS("VP-F"),
  FIJI("DQ"),
  FINLAND("OH"),
  FRANCE("F"),
  FRENCH_GUIANA("F-Ofg"),
  FRENCH_POLYNESIA("F-Opo"),
  GUADELOUPE("F-Ogu"),
  MARTINIQUE("F-Oma"),
  NEW_CALEDONIA("F-Onc"),
  REUNION("F-Ore"),
  ST_PIERRE_AND_MIQUELON("F-Osp"),
  GABON("TR"),
  GAMBIA("C5"),
  GEORGIA("4L"),
  GERMANY("D"),
  GHANA("9G"),
  GIBRALTAR("VP-G"),
  GREECE("SX"),
  GREENLAND("OYg"),
  GRENADA("J3"),
  GUAM("NGU"),
  GUATEMALA("TG"),
  GUINEA("3X"),
  GUINEA_BISSAU("J5"),
  GUYANA("8R"),
  HAITI("HH"),
  HONDURAS("HR"),
  HONG_KONG("VR-H"),
  HUNGARY("HA"),
  ICELAND("TF"),
  INDIA("VT"),
  INDONESIA("PK"),
  IRAN("EP"),
  IRAQ("YI"),
  IRELAND("EI"),
  ISRAEL("4X"),
  ITALY("I"),
  JAMAICA("6Y"),
  JAPAN("JA"),
  JORDAN("JY"),
  KAZAKHSTAN("UN"),
  KENYA("5Y"),
  KIRIBATI("T3"),
  KOSOVO("Z6"),
  KUWAIT("9K"),
  KYRGYZSTAN("EX"),
  LAOS("RDPL"),
  LATVIA("YL"),
  LEBANON("OD"),
  LESOTHO("7P"),
  LIBERIA("EL"),
  LIBYA("5A"),
  LITHUANIA("LY"),
  LUXEMBOURG("LX"),
  MACAU("B-M"),
  MACEDONIA_FYROM("Z3"),
  MADAGASCAR("5R"),
  MALAWI("7Q"),
  MALAYSIA("9M"),
  MALDIVES("8Q"),
  MALI("TZ"),
  MALTA("9H"),
  MARSHALL_ISLANDS("V7"),
  MAURITANIA("5T"),
  MAURITIUS("3B"),
  MEXICO("XA"),
  MICRONESIA("V6"),
  MOLDOVA("ER"),
  MONACO("3A"),
  MONGOLIA("MT"),
  MONTENEGRO("YUM"),
  MONTSERRAT("VP-M"),
  MOROCCO("CN"),
  MOZAMBIQUE("C9"),
  MYANMAR("XY"),
  NAMIBIA("V5"),
  NAURU("C2"),
  NEPAL("9N"),
  NETHERLANDS("PH"),
  NEW_ZEALAND("ZK"),
  NICARAGUA("YN"),
  NIGER("5U"),
  NIGERIA("5N"),
  NORTH_KOREA("P"),
  NORTHERN_MARIANA_ISLANDS("NNM"),
  NORWAY("LN"),
  OMAN("A4O"),
  PAKISTAN("AP"),
  PALAU("T8A"),
  PANAMA("HP"),
  PAPUA_NEW_GUINEA("P2"),
  PARAGUAY("ZP"),
  PERU("OB"),
  PHILIPPINES("RP"),
  POLAND("SP"),
  PORTUGAL("CS"),
  PUERTO_RICO("NPR"),
  QATAR("A7"),
  ROMANIA("YR"),
  RUSSIA("RA"),
  RWANDA("9XR"),
  SAINT_HELENA("GSH"),
  SAINT_LUCIA("J6"),
  SAINT_VINCENT_AND_THE_GRENADINES("J8"),
  SAMOA("5W"),
  SAN_MARINO("T7"),
  SAO_TOME_AND_PRINCIPE("S9"),
  SAUDI_ARABIA("HZ"),
  SENEGAL("6V"),
  SERBIA("YU"),
  SEYCHELLES("S7"),
  SIERRA_LEONE("9L"),
  SINGAPORE("9V"),
  SINT_MAARTEN("PJsm"),
  SLOVAKIA("OM"),
  SLOVENIA("S5"),
  SOLOMON_ISLANDS("H4"),
  SOMALIA("6O"),
  SOUTH_AFRICA("ZS"),
  SOUTH_KOREA("HL"),
  SOUTH_SUDAN("STSS"),
  SPAIN("EC"),
  SRI_LANKA("4R"),
  ST_KITTS_AND_NEVIS("V4"),
  SUDAN("ST"),
  SURINAME("PZ"),
  SWEDEN("SE"),
  SWITZERLAND("HB"),
  SYRIA("YK"),
  TAIWAN("BT"),
  TAJIKISTAN("EY"),
  TANZANIA("5H"),
  THAILAND("HS"),
  TOGO("5V"),
  TONGA("A3"),
  TRINIDAD_AND_TOBAGO("9Y"),
  TUNISIA("TS"),
  TURKEY("TC"),
  TURKMENISTAN("EZ"),
  TURKS_AND_CAICOS_ISLANDS("VQ-T"),
  TUVALU("T2"),
  UGANDA("5X"),
  UKRAINE("UR"),
  UNITED_ARAB_EMIRATES("A6"),
  UNITED_KINGDOM("G"),
  URUGUAY("CX"),
  USA("N"),
  US_MINOR_OUTLYING_ISLANDS("NMO"),
  VIRGIN_ISLANDS_US("NVI"),
  UZBEKISTAN("UK"),
  VANUATU("YJ"),
  VENEZUELA("YV"),
  VIETNAM("VN"),
  WESTERN_SAHARA("EH"),
  YEMEN("7O"),
  ZAMBIA("9J"),
  ZIMBABWE("Z"),
  ANTARCTICA("ant"),
  NORTH_POLE("nop"),
  ATLANTIC_OCEAN("atl"),
  MEDITERRANEAN_SEA("med"),
  INDIAN_OCEAN("ind"),
  PACIFIC_OCEAN("pac"),
  UNKNOWN("unk");

  private final String code;

  AircraftRegistrationCode(String code) {
    this.code = code;
  }

  /**
   * JSON 직렬화 시 코드 값을 사용
   */
  @JsonValue
  public String getCode() {
    return code;
  }

  /**
   * 주어진 문자열(코드)을 Enum 상수로 변환합니다.
   * 대소문자 구분 없이 매핑하며, 매핑되지 않을 경우 IllegalArgumentException을 발생시킵니다.
   */
  @JsonCreator
  public static AircraftRegistrationCode fromString(String code) {
    if (code != null) {
      for (AircraftRegistrationCode value : AircraftRegistrationCode.values()) {
        if (value.code.equalsIgnoreCase(code)) {
          return value;
        }
      }
    }
    throw new IllegalArgumentException("Unknown registration code: " + code);
  }

  /**
   * 문자열을 받아서 Enum 상수로 매핑하는 별도 메소드
   */
  public static AircraftRegistrationCode strToEnum(String code) {
    return fromString(code);
  }
}
