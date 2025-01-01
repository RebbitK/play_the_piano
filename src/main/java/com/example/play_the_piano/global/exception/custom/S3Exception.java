package com.example.play_the_piano.global.exception.custom;

public class S3Exception extends RuntimeException {
	public S3Exception(String message) {
		super(message);
	}
}