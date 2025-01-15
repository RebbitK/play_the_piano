package com.example.play_the_piano.s3file.entity;

public enum TypeEnum {
	IMAGE(Authority.IMAGE),
	FILE(Authority.FILE),
	;
	private final String authority;

	TypeEnum(String authority) {
		this.authority = authority;
	}

	public static class Authority {

		public static final String IMAGE = "IMAGE";
		public static final String FILE = "FILE";
	}
}
