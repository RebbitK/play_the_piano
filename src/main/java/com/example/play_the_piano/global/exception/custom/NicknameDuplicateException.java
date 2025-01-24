package com.example.play_the_piano.global.exception.custom;

public class NicknameDuplicateException extends RuntimeException{

	public NicknameDuplicateException(String message) {
		super(message);
	}
}
