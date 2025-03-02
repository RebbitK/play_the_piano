package com.example.play_the_piano.user.controller;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.global.util.JwtUtil;
import com.example.play_the_piano.user.dto.CheckEmailDto;
import com.example.play_the_piano.user.dto.CheckNicknameDto;
import com.example.play_the_piano.user.dto.CheckUsernameDto;
import com.example.play_the_piano.user.dto.LoginRequestDto;
import com.example.play_the_piano.user.dto.LoginResponseDto;
import com.example.play_the_piano.user.dto.MyPageResponseDto;
import com.example.play_the_piano.user.dto.RoleChangeResponseDto;
import com.example.play_the_piano.user.dto.SendEmailDto;
import com.example.play_the_piano.user.dto.SignupRequestDto;
import com.example.play_the_piano.user.dto.SignupResponseDto;
import com.example.play_the_piano.user.dto.UpdatePasswordDto;
import com.example.play_the_piano.user.entity.UserDetailsImpl;
import com.example.play_the_piano.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class UserController {

	private final UserService userService;

	private final JwtUtil jwtUtil;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<SignupResponseDto>> singUp(
		@Valid @RequestBody SignupRequestDto requestDto) {
		SignupResponseDto responseDto = userService.insertUser(requestDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<SignupResponseDto>builder()
				.msg("signup successful")
				.data(responseDto)
				.build());
	}

	@PostMapping("/login")
	public ResponseEntity<CommonResponse<LoginResponseDto>> login(
		@RequestBody LoginRequestDto requestDto,
		HttpServletResponse response) {
		LoginResponseDto responseDto = userService.login(requestDto);
		response.setHeader(JwtUtil.AUTHORIZATION_HEADER,
			jwtUtil.createToken(responseDto.getId(), responseDto.getUsername(),
				responseDto.getNickname(), responseDto.getRole()));
		response.setHeader(JwtUtil.REFRESH_TOKEN_HEADER,
			jwtUtil.createClientToken(responseDto.getId()));
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<LoginResponseDto>builder()
				.msg("login successful")
				.data(responseDto)
				.build());
	}

	@PostMapping("/check-username")
	public ResponseEntity<CommonResponse<Boolean>> checkUsername(
		@Valid @RequestBody CheckUsernameDto usernameDto) {
		Boolean check = userService.checkUsername(usernameDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("check password successful")
				.data(check)
				.build());
	}

	@PostMapping("/check-nickname")
	public ResponseEntity<CommonResponse<Boolean>> checkNickname(
		@Valid @RequestBody CheckNicknameDto nicknameDto) {
		Boolean check = userService.checkNickname(nicknameDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("check nickname successful")
				.data(check)
				.build());
	}

	@PostMapping("/signup/send-email")
	public ResponseEntity<CommonResponse<Boolean>> sendSignupEmail(
		@Valid @RequestBody SendEmailDto emailDto) {
		Boolean check = userService.sendSignupEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("send email successful")
				.data(check)
				.build());
	}

	@PostMapping("/check-email-verification")
	public ResponseEntity<CommonResponse<Boolean>> sendEmailVerification(
		@Valid @RequestBody CheckEmailDto emailDto) {
		Boolean check = userService.checkSendSignupEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("check email successful")
				.data(check)
				.build());
	}

	@PostMapping("/username/send-email")
	public ResponseEntity<CommonResponse<Boolean>> sendFindUsernameEmail(
		@Valid @RequestBody SendEmailDto emailDto) {
		Boolean check = userService.sendFindUsernameEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("send email successful")
				.data(check)
				.build());
	}

	@PostMapping("/find-username")
	public ResponseEntity<CommonResponse<String>> findUsernameByEmail(
		@Valid @RequestBody CheckEmailDto emailDto) {
		String username = userService.findUsernameByEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<String>builder()
				.msg("find username successful")
				.data(username)
				.build());
	}

	@PostMapping("/password/send-email")
	public ResponseEntity<CommonResponse<Boolean>> sendFindPasswordEmail(
		@Valid @RequestBody SendEmailDto emailDto) {
		Boolean check = userService.sendFindPasswordEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("send email successful")
				.data(check)
				.build());
	}

	@PostMapping("/find-password")
	public ResponseEntity<CommonResponse<Boolean>> findPasswordByEmail(
		@Valid @RequestBody CheckEmailDto emailDto) {
		Boolean check = userService.findPasswordByEmail(emailDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("find password successful")
				.data(check)
				.build());
	}

	@PatchMapping("/update-password")
	public ResponseEntity<CommonResponse<Boolean>> updatePassword(
		@Valid @RequestBody UpdatePasswordDto passwordDto) {
		Boolean check = userService.updatePassword(passwordDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("update password successful")
				.data(check)
				.build());
	}

	@GetMapping("/myPage")
	public ResponseEntity<CommonResponse<MyPageResponseDto>> getMyPage(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		MyPageResponseDto responseDto = userService.getMyPage(userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<MyPageResponseDto>builder()
				.msg("get MyPage successful")
				.data(responseDto)
				.build());
	}

	@PatchMapping("/update-nickname")
	public ResponseEntity<CommonResponse<Void>> updateNickname(
		@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody  CheckNicknameDto nicknameDto) {
		userService.updateNickname(userDetails.getUser(), nicknameDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("update nickname successful")
				.build());
	}

	@PatchMapping("/update-username")
	public ResponseEntity<CommonResponse<Void>> updateUsername(
		@AuthenticationPrincipal UserDetailsImpl userDetails,  @RequestBody CheckUsernameDto usernameDto) {
		userService.updateUsername(userDetails.getUser(), usernameDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("update username successful")
				.build());
	}

	@PostMapping("/role/change/request")
	public ResponseEntity<CommonResponse<Void>> createRoleChangeRequest(
		@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String content) {
		userService.createRoleChangeRequest(userDetails.getUser(), content);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("roleChange request successful")
				.build());
	}

	@GetMapping("/role/change/request")
	public ResponseEntity<CommonResponse<Boolean>> getRoleChangeRequest(@AuthenticationPrincipal UserDetailsImpl userDetails){
		Boolean check = userService.getRoleChangeRequest(userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.data(check)
				.msg("get roleChange request successful")
				.build());
	}

	@PatchMapping("/role/change/request/{id}/approve")
	public ResponseEntity<CommonResponse<Void>> updateUserRole(
		@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
		userService.updateUserRole(userDetails.getUser(), id);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("roleChange request successful")
				.build());
	}

	@GetMapping("/role/change/requests")
	public ResponseEntity<CommonResponse<List<RoleChangeResponseDto>>> getRoleChangeRequests(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "10") int size) {
		List<RoleChangeResponseDto> responseDtos = userService.getRoleChangeRequests(userDetails.getUser(), page, size);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<List<RoleChangeResponseDto>>builder()
				.data(responseDtos)
				.msg("get roleChange requests successful")
				.build());
	}

	@PostMapping("/delete/send-email")
	public ResponseEntity<CommonResponse<Void>> sendDeleteAccountEmail(@AuthenticationPrincipal UserDetailsImpl userDetails){
		userService.sendDeleteAccountEmail(userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("send delete email successful")
				.build());
	}

	@DeleteMapping("/delete")
	public ResponseEntity<CommonResponse<Void>> deleteAccount(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestBody String code){
		userService.deleteAccount(userDetails.getUser(), code);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("delete account successful")
				.build());
	}

}
