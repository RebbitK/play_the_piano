package com.example.play_the_piano.global.exception.custom;

public class PasswordUpdateFailedException extends RuntimeException {
	public PasswordUpdateFailedException(String message) {
		super(message);
	}
}