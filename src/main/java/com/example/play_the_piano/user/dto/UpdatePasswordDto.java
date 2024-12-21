package com.example.play_the_piano.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdatePasswordDto {

	@NotBlank(message = "이메일은 필수 입력사항 입니다.")
	@Pattern(regexp = "^(?!.*\\.\\.)[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "올바르지 않은 형식의 이메일 주소 입니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Pattern(regexp = "(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,20}",
		message = "비밀번호는 영어와 숫자가 각각 1개 이상 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
	private String password;

	private String checkPassword;

}
