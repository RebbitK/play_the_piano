package com.example.play_the_piano.global.exception.custom;

public class UsernameDuplicateException extends RuntimeException {

	public UsernameDuplicateException(String message) {
		super(message);
	}
}
