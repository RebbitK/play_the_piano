package com.example.play_the_piano.global.exception.custom;

public class EmailAlreadyRegisteredException extends RuntimeException {
	public EmailAlreadyRegisteredException(String message) {
		super(message);
	}
}
