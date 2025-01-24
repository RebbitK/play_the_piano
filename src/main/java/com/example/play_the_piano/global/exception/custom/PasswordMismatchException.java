package com.example.play_the_piano.global.exception.custom;

public class PasswordMismatchException extends RuntimeException {

	public PasswordMismatchException(String message) {
		super(message);
	}

}