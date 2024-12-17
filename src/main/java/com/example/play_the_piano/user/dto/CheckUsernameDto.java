package com.example.play_the_piano.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CheckUsernameDto {

	@NotBlank(message = "아이디는 필수 입력값입니다.")
	@Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영문 소문자와 숫자 4~12자리여야 합니다.")
	private String username;

}
