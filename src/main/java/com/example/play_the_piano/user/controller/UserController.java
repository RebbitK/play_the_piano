package com.example.play_the_piano.user.controller;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.global.util.JwtUtil;
import com.example.play_the_piano.user.dto.CheckEmailDto;
import com.example.play_the_piano.user.dto.CheckNicknameDto;
import com.example.play_the_piano.user.dto.CheckUsernameDto;
import com.example.play_the_piano.user.dto.LoginRequestDto;
import com.example.play_the_piano.user.dto.LoginResponseDto;
import com.example.play_the_piano.user.dto.SendEmailDto;
import com.example.play_the_piano.user.dto.SignupRequestDto;
import com.example.play_the_piano.user.dto.SignupResponseDto;
import com.example.play_the_piano.user.dto.UpdatePasswordDto;
import com.example.play_the_piano.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
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

	@PostMapping("/signup/send-email")
	public ResponseEntity<CommonResponse<Boolean>> sendSignupEmail(@Valid @RequestBody SendEmailDto emailDto){
		Boolean check = userService.sendSignupEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("send email successful")
				.data(check)
				.build());
	}

	@PostMapping("/check-email-verification")
	public ResponseEntity<CommonResponse<Boolean>> sendEmailVerification(@Valid @RequestBody CheckEmailDto emailDto){
		Boolean check = userService.checkSendSignupEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("check email successful")
				.data(check)
				.build());
	}

	@PostMapping("/username/send-email")
	public ResponseEntity<CommonResponse<Boolean>> sendFindUsernameEmail(@Valid @RequestBody SendEmailDto emailDto){
		Boolean check = userService.sendFindUsernameEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("send email successful")
				.data(check)
				.build());
	}

	@PostMapping("/find-username")
	public ResponseEntity<CommonResponse<String>> findUsernameByEmail(@Valid @RequestBody CheckEmailDto emailDto){
		String username = userService.findUsernameByEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<String>builder()
				.msg("find username successful")
				.data(username)
				.build());
	}

	@PostMapping("/password/send-email")
	public ResponseEntity<CommonResponse<Boolean>> sendFindPasswordEmail(@Valid @RequestBody SendEmailDto emailDto){
		Boolean check = userService.sendFindPasswordEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("send email successful")
				.data(check)
				.build());
	}

	@PostMapping("/find-password")
	public ResponseEntity<CommonResponse<Boolean>> findPasswordByEmail(@Valid @RequestBody CheckEmailDto emailDto){
		Boolean check = userService.findPasswordByEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("find password successful")
				.data(check)
				.build());
	}

	@PatchMapping("/update-password")
	public ResponseEntity<CommonResponse<Boolean>> updatePassword(@Valid @RequestBody UpdatePasswordDto passwordDto){
		Boolean check = userService.updatePassword(passwordDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("update password successful")
				.data(check)
				.build());
	}
}
