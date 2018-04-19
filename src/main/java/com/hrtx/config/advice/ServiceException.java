package com.hrtx.config.advice;

public class ServiceException extends RuntimeException {
	private String s = "";
	
	public ServiceException(String s) {
		this.s = s;
	}
	
	@Override
	public String getMessage() {
		return s;
	}
}
