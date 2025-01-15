package com.example.play_the_piano.global.exception.custom;

public class QuizNotFoundException extends RuntimeException {
	public QuizNotFoundException(String message) {
		super(message);
	}
}