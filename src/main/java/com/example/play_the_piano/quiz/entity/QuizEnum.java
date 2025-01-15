package com.example.play_the_piano.quiz.entity;

public enum QuizEnum {
	BEGINNER(QuizEnum.Authority.BEGINNER),
	INTERMEDIATE(QuizEnum.Authority.INTERMEDIATE),
	ADVANCED (QuizEnum.Authority.ADVANCED),
	;
	private final String authority;

	QuizEnum(String authority) {
		this.authority = authority;
	}

	public static class Authority {

		public static final String BEGINNER = "BEGINNER";
		public static final String INTERMEDIATE = "INTERMEDIATE";
		public static final String ADVANCED = "ADVANCED";
	}
}
