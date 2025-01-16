package com.example.play_the_piano.s3file.entity;

public enum ObjectEnum {
	POST(ObjectEnum.Authority.POST),
	QUIZ(ObjectEnum.Authority.QUIZ),
	;
	private final String authority;

	ObjectEnum(String authority) {
		this.authority = authority;
	}

	public static class Authority {

		public static final String POST = "POST";
		public static final String QUIZ = "QUIZ";
	}
}
