package com.example.play_the_piano.user.controller;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.global.util.JwtUtil;
import com.example.play_the_piano.user.dto.CheckEmailDto;
import com.example.play_the_piano.user.dto.CheckNicknameDto;
import com.example.play_the_piano.user.dto.CheckUsernameDto;
import com.example.play_the_piano.user.dto.LoginRequestDto;
import com.example.play_the_piano.user.dto.LoginResponseDto;
import com.example.play_the_piano.user.dto.SignupRequestDto;
import com.example.play_the_piano.user.dto.SignupResponseDto;
import com.example.play_the_piano.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/auth")
@Slf4j
public class UserController {

	private final UserService userService;

	private final JwtUtil jwtUtil;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<SignupResponseDto>> singUp(@Valid @RequestBody SignupRequestDto requestDto){
		SignupResponseDto responseDto = userService.insertUser(requestDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<SignupResponseDto>builder()
				.msg("signup successful")
				.data(responseDto)
				.build());
	}

	@PostMapping("/login")
	public ResponseEntity<CommonResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto requestDto,
		HttpServletResponse response){
		LoginResponseDto responseDto = userService.login(requestDto);
		response.setHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(responseDto.getId(),responseDto.getUsername(),responseDto.getNickname(),responseDto.getRole()));
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<LoginResponseDto>builder()
				.msg("login successful")
				.data(responseDto)
				.build());
	}

	@PostMapping("/check-username")
	public ResponseEntity<CommonResponse<Boolean>> checkUsername(@Valid @RequestBody CheckUsernameDto usernameDto){
		Boolean check = userService.checkUsername(usernameDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("check password successful")
				.data(check)
				.build());
	}

	@PostMapping("/check-nickname")
	public ResponseEntity<CommonResponse<Boolean>> checkNickname(@Valid @RequestBody CheckNicknameDto nicknameDto){
		Boolean check = userService.checkNickname(nicknameDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("check nickname successful")
				.data(check)
				.build());
	}

	@PostMapping("/check-email")
	public ResponseEntity<CommonResponse<Boolean>> checkEmail(@Valid @RequestBody CheckEmailDto emailDto){
		Boolean check = userService.checkEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("check email successful")
				.data(check)
				.build());
	}
}
