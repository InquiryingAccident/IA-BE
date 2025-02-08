package com.suhkang.inquiryingaccident.util.log;

import com.suhkang.inquiryingaccident.object.constants.Author;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiChangeLog {

  String date();

  Author author();

  String description();
}
