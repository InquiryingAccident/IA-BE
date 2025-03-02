package com.suhkang.inquiryingaccident.global.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ScrapingUtil {

  /**
   * 주어진 Document에서 id로 요소를 찾아 텍스트를 반환합니다.
   */
  public static String getTextById(Document doc, String id) {
    Element el = doc.getElementById(id);
    return el != null ? el.text().trim() : "";
  }

  /**
   * 주어진 Document에서 id로 요소를 찾아 지정한 속성값을 반환합니다.
   */
  public static String getAttrById(Document doc, String id, String attr) {
    Element el = doc.getElementById(id);
    return el != null ? el.attr(attr).trim() : "";
  }
}