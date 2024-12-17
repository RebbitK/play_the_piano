package com.example.play_the_piano.user.dto;

import com.example.play_the_piano.user.entity.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDto {

	Long id;

	String username;

	String nickname;

	RoleEnum role;
}
