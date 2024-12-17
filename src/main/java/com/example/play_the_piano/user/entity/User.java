package com.example.play_the_piano.user.entity;

import com.example.play_the_piano.user.dto.SignupRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	private String nickname;

	private String password;

	@Enumerated(EnumType.STRING)
	private RoleEnum role;

	private String email;

	private boolean consent;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;

	public User(Long userId, String username, String nickname, String role) {
		this.id = userId;
		this.username = username;
		this.nickname = nickname;
		this.role = RoleEnum.valueOf(role);
	}

	public User(SignupRequestDto requestDto, String encodedPassword) {
		this.username = requestDto.getUsername();
		this.nickname = requestDto.getNickname();
		this.password = encodedPassword;
		this.role = RoleEnum.CUSTOMER;
		this.email = requestDto.getEmail();
		this.consent = requestDto.isConsent();
	}
}