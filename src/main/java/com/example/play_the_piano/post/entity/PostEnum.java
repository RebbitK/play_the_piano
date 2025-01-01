package com.example.play_the_piano.post.entity;

import lombok.Getter;

@Getter
public enum PostEnum {
	ANNOUNCEMENT(PostEnum.Authority.ANNOUNCEMENT),
	EVENT(PostEnum.Authority.EVENT),
	NORMAL(PostEnum.Authority.NORMAL);
	private final String authority;

	PostEnum(String authority) {
		this.authority = authority;
	}

	public static class Authority {

		public static final String ANNOUNCEMENT = "POST_ANNOUNCEMENT";
		public static final String EVENT = "POST_EVENT";
		public static final String NORMAL = "POST_NORMAL";
	}
}
