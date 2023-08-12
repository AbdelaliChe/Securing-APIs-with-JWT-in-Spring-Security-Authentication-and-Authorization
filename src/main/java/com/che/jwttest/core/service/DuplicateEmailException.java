package com.che.jwttest.core.service;

public class DuplicateEmailException extends RuntimeException{
	public DuplicateEmailException(String message) {
		super(message);
	}
}
