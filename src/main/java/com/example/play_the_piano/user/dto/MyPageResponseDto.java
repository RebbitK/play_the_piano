package com.example.play_the_piano.user.dto;

import com.example.play_the_piano.user.entity.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MyPageResponseDto {
	
	private String nickname;

	@Enumerated(EnumType.STRING)
	private RoleEnum role;

}
