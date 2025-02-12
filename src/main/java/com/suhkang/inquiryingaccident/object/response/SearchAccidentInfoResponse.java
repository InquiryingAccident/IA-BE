package com.suhkang.inquiryingaccident.object.response;

import com.suhkang.inquiryingaccident.object.dao.Accident;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchAccidentInfoResponse {
  private Page<Accident> accidentPage;
}