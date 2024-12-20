package com.example.play_the_piano.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CheckEmailDto {

	@NotBlank(message = "이메일은 필수 입력사항 입니다.")
	@Pattern(regexp = "^(?!.*\\.\\.)[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "올바르지 않은 형식의 이메일 주소 입니다.")
	private String email;

}
