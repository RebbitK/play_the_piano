package com.example.play_the_piano.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SingUpRequestDto {

	@NotBlank(message = "아이디는 필수 입력값입니다.")
	@Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영문 소문자와 숫자 4~12자리여야 합니다.")
	private String username;

	@NotBlank(message = "닉네임은 필수 입력값입니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "닉네임은 특수문자를 포함하지 않은 2~10자리여야 합니다.")
	private String nickname;

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Pattern(regexp="(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,20}",
		message = "비밀번호는 영어와 숫자가 각각 1개 이상 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
	private String password;

	private String phoneNumber;

	private boolean consent;
}
