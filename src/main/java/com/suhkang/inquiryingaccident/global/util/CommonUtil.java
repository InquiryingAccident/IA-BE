package com.suhkang.inquiryingaccident.global.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Random;

public class CommonUtil {
	private final Random random = new Random();

	private int getRandomNumber(int min, int max) {
		if (min == max) {
			return min;
		}
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * 문자열 SHA-256 해시 계산
	 */
	public static String calculateSha256ByStr(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("SHA-256 해시 계산 실패", e);
		}
	}

	/**
	 * 파일 SHA-256 해시값 계산
	 */
	public static String calculateFileHash(Path filePath) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] fileBytes = Files.readAllBytes(filePath);
			byte[] hashBytes = digest.digest(fileBytes);
			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("파일 해시 계산 실패", e);
		}
	}

	/*
	 * null 또는 "null"/빈 문자열을 빈 문자열("")로 치환
	 */
	public static String nvl(Object str) {
		return nvl(String.valueOf(str), "");
	}

	public static String nvl(String str) {
		return nvl(str, "");
	}

	public static String nvl(Object str, String str1) {
		return nvl(String.valueOf(str), str1);
	}

	public static String nvl(String str, String str1) {
		if (null == str) {
			return str1;
		} else if ("null".equals(str)) {
			return str1;
		} else if ("".equals(str)) {
			return str1;
		}
		return str;
	}

	/**
	 * 문자열을 정수로 변환합니다. 숫자가 아닌 문자는 제거 후 파싱.
	 */
	public static Integer parseInteger(String text) {
		try {
			return Integer.parseInt(text.replaceAll("[^0-9]", ""));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 문자열을 실수로 변환합니다. 숫자와 소수점 외 문자는 제거 후 파싱.
	 */
	public static Double parseDouble(String text) {
		try {
			return Double.parseDouble(text.replaceAll("[^0-9\\.]", ""));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 문자열에서 숫자와 소수점을 제외한 모든 문자를 제거합니다.
	 */
	public static String removeUnit(String text) {
		return text.replaceAll("[^0-9\\.]", "");
	}
}