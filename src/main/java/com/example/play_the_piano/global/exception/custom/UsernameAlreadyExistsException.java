package com.example.play_the_piano.global.exception.custom;

public class UsernameAlreadyExistsException extends RuntimeException {

	public UsernameAlreadyExistsException(String message) {
		super(message);
	}
}