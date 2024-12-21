package com.example.play_the_piano.user.service;

import com.example.play_the_piano.global.exception.custom.EmailAlreadyRegisteredException;
import com.example.play_the_piano.global.exception.custom.InvalidAuthCodeException;
import com.example.play_the_piano.global.exception.custom.PasswordUpdateFailedException;
import com.example.play_the_piano.global.exception.custom.SendEmailException;
import com.example.play_the_piano.global.util.RedisUtil;
import com.example.play_the_piano.user.dto.CheckEmailDto;
import com.example.play_the_piano.user.dto.CheckNicknameDto;
import com.example.play_the_piano.user.dto.CheckUsernameDto;
import com.example.play_the_piano.user.dto.LoginRequestDto;
import com.example.play_the_piano.user.dto.LoginResponseDto;
import com.example.play_the_piano.user.dto.SendEmailDto;
import com.example.play_the_piano.user.dto.SignupRequestDto;
import com.example.play_the_piano.user.dto.SignupResponseDto;
import com.example.play_the_piano.user.dto.UpdatePasswordDto;
import com.example.play_the_piano.user.entity.User;
import com.example.play_the_piano.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JavaMailSender mailSender;

	private final SpringTemplateEngine templateEngine;

	private final RedisUtil redisUtil;

	@Transactional
	@Async
	public void sendEmail(String to, String subject, String text,String code,String key) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, true);

			mailSender.send(message);

			redisUtil.setDataExpire(key+to,code,1000L * 60L * 5);
		} catch (Exception e) {
			throw new SendEmailException("인증 메일 발송 중 오류가 발생하였습니다.");
		}
	}

	public boolean checkUsername(CheckUsernameDto usernameDto) {
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

	public boolean checkSendSignupEmail(CheckEmailDto emailDto) {
		if(redisUtil.getData("Auth:"+emailDto.getEmail())==null||redisUtil.getData("Auth:"+emailDto.getEmail()).isEmpty()){
			throw new InvalidAuthCodeException("유효하지 않은 이메일 입니다.");
		}
		if(emailDto.getCode()==null||!emailDto.getCode().equals(redisUtil.getData("Auth:"+emailDto.getEmail()))){
			throw new InvalidAuthCodeException("유효하지 않은 인증번호 입니다.");
		}
		return true;
	}

	public boolean sendSignupEmail(SendEmailDto emailDto) {

		String code = createCode();
			sendEmail(emailDto.getEmail(), "Play The Piano", setContext(code, "email-signup"),code,"Auth:");
		return true;
	}


	public String setContext(String code, String type) {
		Context context = new Context();
		context.setVariable("code", code);
		return templateEngine.process(type, context);
	}

	public String createCode() {
		Random random = new Random();
		StringBuilder key = new StringBuilder();

		for (int i = 0; i < 8; i++) {
			int index = random.nextInt(4);

			switch (index) {
				case 0:
					key.append((char) (random.nextInt(26) + 'a'));
					break;
				case 1:
					key.append((char) (random.nextInt(26) + 'A'));
					break;
				default:
					key.append(random.nextInt(10));
			}
		}
		return key.toString();
	}

	@Transactional
	public SignupResponseDto insertUser(SignupRequestDto requestDto) {
		if (requestDto.getUsername().isEmpty()) {
			throw new IllegalArgumentException("아이디 중복 체크를 확인해 주세요.");
		}
		if (requestDto.getNickname().isEmpty()) {
			throw new IllegalArgumentException("닉네임 중복 체크를 확인해 주세요.");
		}
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

	public boolean sendFindUsernameEmail(SendEmailDto emailDto){
		String code = createCode();
		if(userRepository.findByEmail(emailDto.getEmail()).isPresent()){
			throw new EmailAlreadyRegisteredException("이 이메일로 이미 가입된 계정이 있습니다.");
		}
		sendEmail(emailDto.getEmail(), "Play The Piano", setContext(code, "email-findUsername"),code,"findUsername:");
		return true;
	}

	public String findUsernameByEmail(CheckEmailDto emailDto){
		if(redisUtil.getData("findUsername:"+emailDto.getEmail())==null||redisUtil.getData("findUsername:"+emailDto.getEmail()).isEmpty()){
			throw new InvalidAuthCodeException("유효하지 않은 이메일 입니다.");
		}
		if(emailDto.getCode()==null||!emailDto.getCode().equals(redisUtil.getData("findUsername:"+emailDto.getEmail()))){
			throw new InvalidAuthCodeException("유효하지 않은 인증번호 입니다.");
		}
		return userRepository.findUsernameByEmail(emailDto.getEmail()).orElseThrow(
			()-> new IllegalArgumentException("이 이메일로 가입된 아이디가 없습니다.")
		);
	}

	public boolean sendFindPasswordEmail(SendEmailDto emailDto){
		String code = createCode();
		sendEmail(emailDto.getEmail(), "Play The Piano", setContext(code, "email-findPassword"),code,"findPassword:");
		return true;
	}

	public boolean findPasswordByEmail(CheckEmailDto emailDto){
		if(redisUtil.getData("findPassword:"+emailDto.getEmail())==null||redisUtil.getData("findPassword:"+emailDto.getEmail()).isEmpty()){
			throw new InvalidAuthCodeException("유효하지 않은 이메일 입니다.");
		}
		if(emailDto.getCode()==null||!emailDto.getCode().equals(redisUtil.getData("findPassword:"+emailDto.getEmail()))){
			throw new InvalidAuthCodeException("유효하지 않은 인증번호 입니다.");
		}
		return userRepository.findByEmail(emailDto.getEmail()).orElseThrow(
			()-> new IllegalArgumentException("이 이메일로 가입된 아이디가 없습니다.")
		);
	}

	@Transactional
	public boolean updatePassword(UpdatePasswordDto passwordDto){
		Long id = userRepository.findIdByEmail(passwordDto.getEmail()).orElseThrow(
			()-> new InvalidAuthCodeException("유효하지 않은 이메일 입니다.")
		);
		if (!passwordDto.getPassword().equals(passwordDto.getCheckPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		String encodedPassword = passwordEncoder.encode(passwordDto.getPassword());
		int update = userRepository.updatePassword(id,encodedPassword).orElseThrow(
			()-> new PasswordUpdateFailedException("비밀번호 변경 중 오류가 발생했습니다.")
		);
		return update == 1;
	}
}
