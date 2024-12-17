package com.example.play_the_piano.user.service;

import com.example.play_the_piano.user.dto.CheckNicknameDto;
import com.example.play_the_piano.user.dto.CheckUsernameDto;
import com.example.play_the_piano.user.dto.LoginRequestDto;
import com.example.play_the_piano.user.dto.LoginResponseDto;
import com.example.play_the_piano.user.dto.SignupRequestDto;
import com.example.play_the_piano.user.dto.SignupResponseDto;
import com.example.play_the_piano.user.entity.User;
import com.example.play_the_piano.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public boolean checkUsername(CheckUsernameDto usernameDto) {
		System.out.println(usernameDto.getUsername()+"tt");
		Boolean check = userRepository.existsByUsername(usernameDto.getUsername()).orElseThrow(
			() -> new NullPointerException("아이디를 입력해 주세요.")
		);
		if (check) {
			throw new IllegalArgumentException("이미 존재하는 아이디 입니다.");
		} else {
			return true;
		}
	}

	public boolean checkNickname(CheckNicknameDto nicknameDto) {
		Boolean check = userRepository.existsByNickname(nicknameDto.getNickname()).orElseThrow(
			() -> new NullPointerException("닉네임을 입력해 주세요.")
		);
		if (check) {
			throw new IllegalArgumentException("이미 존재하는 닉네임 입니다.");
		} else {
			return true;
		}
	}

	public SignupResponseDto insertUser(SignupRequestDto requestDto) {
		if (requestDto.getUsername().isEmpty()) {
			throw new IllegalArgumentException("아이디 중복 체크를 확인해 주세요.");
		}
		if (requestDto.getNickname().isEmpty()) {
			throw new IllegalArgumentException("닉네임 중복 체크를 확인해 주세요.");
		}
		System.out.println(requestDto.getPassword()+"AAAAAAAAAAAAAA"+requestDto.getCheckPassword());
		if (!requestDto.getPassword().equals(requestDto.getCheckPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
		User user = new User(requestDto, encodedPassword);
		userRepository.insertUser(user);
		return new SignupResponseDto(requestDto.getUsername(), requestDto.getNickname());
	}

	public LoginResponseDto login(LoginRequestDto requestDto) {
		User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 아이디 입니다."));
		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 다릅니다.");
		}
		return new LoginResponseDto(user.getId(), user.getUsername(), user.getNickname(),
			user.getRole());
	}
}
