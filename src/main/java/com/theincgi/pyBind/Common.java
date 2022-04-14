package com.theincgi.pyBind;

public class Common {
	public static String expected(String type, String got) {
		return "expected %s, got %s".formatted(type, got);
	}
}
