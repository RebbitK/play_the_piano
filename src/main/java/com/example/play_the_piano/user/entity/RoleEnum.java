package com.example.play_the_piano.user.entity;

import lombok.Getter;

@Getter
public enum RoleEnum {
	STUDENT(Authority.STUDENT),
	CUSTOMER(Authority.CUSTOMER),
	ADMIN(Authority.ADMIN);

	private final String authority;

	RoleEnum(String authority) {
		this.authority = authority;
	}

	public static class Authority {

		public static final String STUDENT = "ROLE_STUDENT";
		public static final String CUSTOMER = "ROLE_CUSTOMER";
		public static final String ADMIN = "ROLE_ADMIN";
	}
}