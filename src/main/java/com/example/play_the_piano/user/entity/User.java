package com.example.play_the_piano.user.entity;

import com.example.play_the_piano.global.entity.TimeStamped;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	private String password;

	@Enumerated(EnumType.STRING)
	private RoleEnum role;

	private String phoneNumber;

	private boolean consent;

	public User(Long userId, String username, String role) {
		this.id = userId;
		this.username = username;
		this.role = RoleEnum.valueOf(role);
	}
}