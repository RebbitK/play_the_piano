package com.example.play_the_piano.user.entity;

import com.example.play_the_piano.user.dto.SingUpRequestDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
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
import org.springframework.data.annotation.LastModifiedDate;

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

	private String phoneNumber;

	private boolean consent;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;

	public User(Long userId, String username, String role) {
		this.id = userId;
		this.username = username;
		this.role = RoleEnum.valueOf(role);
	}

	public User(SingUpRequestDto requestDto){
		this.username = requestDto.getUsername();
		this.nickname = requestDto.getNickname();
		this.password = requestDto.getPassword();
		this.role = RoleEnum.CUSTOMER;
		this.phoneNumber = requestDto.getPhoneNumber();
		this.consent = requestDto.isConsent();
	}
}