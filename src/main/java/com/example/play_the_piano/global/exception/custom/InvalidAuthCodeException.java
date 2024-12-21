package com.example.play_the_piano.global.exception.custom;

public class InvalidAuthCodeException extends RuntimeException {
	public InvalidAuthCodeException(String message) {
		super(message);
	}
}