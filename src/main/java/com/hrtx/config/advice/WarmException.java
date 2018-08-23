package com.hrtx.config.advice;

public class WarmException extends RuntimeException {
	private String s = "";

	public WarmException(String s) {
		this.s = s;
	}
	
	@Override
	public String getMessage() {
		return s;
	}
}
