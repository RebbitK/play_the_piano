package com.example.play_the_piano.global.exception.custom;

public class QuizIdMismatchException extends RuntimeException {
	public QuizIdMismatchException(String message) {
		super(message);
	}
}