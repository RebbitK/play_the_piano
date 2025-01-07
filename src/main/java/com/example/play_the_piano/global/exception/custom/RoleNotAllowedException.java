package com.example.play_the_piano.global.exception.custom;

public class RoleNotAllowedException extends RuntimeException{
	public RoleNotAllowedException(String message) {
		super(message);
	}

}
