package com.example.play_the_piano.global.exception.custom;

public class SendEmailException extends RuntimeException {

	public SendEmailException(String message) {
		super(message);
	}

}
