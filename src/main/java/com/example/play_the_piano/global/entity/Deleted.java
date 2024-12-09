package com.example.play_the_piano.global.entity;

public enum Deleted {

	DELETE(Authority.DELETE),

	UNDELETE(Authority.UNDELETE);

	private final String authority;

	Deleted(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}

	public static class Authority {
		public static final String DELETE = "DELETE";
		public static final String UNDELETE = "UNDELETE";
	}

}
