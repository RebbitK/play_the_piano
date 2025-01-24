package com.example.play_the_piano.global.exception.custom;

public class NicknameAlreadyExistsException extends RuntimeException {

	public NicknameAlreadyExistsException(String message) {
		super(message);
	}
}