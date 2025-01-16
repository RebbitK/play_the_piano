package com.example.play_the_piano.quiz.entity;

public enum QuizLevel {
	ONE(QuizLevel.Authority.ONE),
	TWO(QuizLevel.Authority.TWO),
	THREE(QuizLevel.Authority.THREE),
	FOUR(QuizLevel.Authority.FOUR),
	FIVE(QuizLevel.Authority.FIVE),
	SIX(QuizLevel.Authority.SIX),
	SEVEN(QuizLevel.Authority.SEVEN),
	EIGHT(QuizLevel.Authority.EIGHT),
	NINE(QuizLevel.Authority.NINE),
	TEN(QuizLevel.Authority.TEN),
	;

	QuizLevel(String authority) {
	}

	public static class Authority {
		public static final String ONE = "ONE";
		public static final String TWO = "TWO";
		public static final String THREE = "THREE";
		public static final String FOUR = "FOUR";
		public static final String FIVE = "FIVE";
		public static final String SIX = "SIX";
		public static final String SEVEN = "SEVEN";
		public static final String EIGHT = "EIGHT";
		public static final String NINE = "NINE";
		public static final String TEN = "TEN";
	}
}
