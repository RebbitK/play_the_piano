package com.example.play_the_piano.global.exception.custom;

public class InvalidPositionException extends RuntimeException {

	public InvalidPositionException(String message) {
		super(message);
	}
}