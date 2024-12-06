package com.example.play_the_piano.global.security;

import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomAuthentication implements Authentication {

	private final UserDetails userDetails;
	private boolean authenticated;

	public CustomAuthentication(UserDetails userDetails) {
		this.userDetails = userDetails;

		this.authenticated = true; // 사용자가 인증된 경우
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userDetails.getAuthorities();
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null; // 기타 인증 정보
	}

	@Override
	public Object getPrincipal() {
		return userDetails; // 사용자 정보
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
	}

	@Override
	public String getName() {
		return userDetails.getUsername(); // 사용자 이름
	}
}
