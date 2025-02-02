package com.example.play_the_piano.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoleChangeResponseDto {

	private Long id;

	private String username;

	private String nickname;

	private String content;

}
