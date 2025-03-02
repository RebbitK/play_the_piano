package com.example.play_the_piano.user.service;

import com.example.play_the_piano.global.exception.custom.EmailAlreadyRegisteredException;
import com.example.play_the_piano.global.exception.custom.InvalidAuthCodeException;
import com.example.play_the_piano.global.exception.custom.NicknameAlreadyExistsException;
import com.example.play_the_piano.global.exception.custom.NicknameDuplicateException;
import com.example.play_the_piano.global.exception.custom.PasswordMismatchException;
import com.example.play_the_piano.global.exception.custom.RoleNotAllowedException;
import com.example.play_the_piano.global.exception.custom.SendEmailException;
import com.example.play_the_piano.global.exception.custom.UserNotFoundException;
import com.example.play_the_piano.global.exception.custom.UsernameAlreadyExistsException;
import com.example.play_the_piano.global.exception.custom.UsernameDuplicateException;
import com.example.play_the_piano.global.util.RedisUtil;
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
import com.example.play_the_piano.user.entity.RoleChangeRequest;
import com.example.play_the_piano.user.entity.RoleEnum;
import com.example.play_the_piano.user.entity.User;
import com.example.play_the_piano.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.util.List;
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
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JavaMailSender mailSender;

	private final SpringTemplateEngine templateEngine;

	private final RedisUtil redisUtil;

	@Transactional
	@Async
	public void sendEmail(String to, String subject, String text, String code, String key) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, true);

			mailSender.send(message);

			redisUtil.setDataExpire(key + to, code, 1000L * 60L * 5);
		} catch (Exception e) {
			throw new SendEmailException("인증 메일 발송 중 오류가 발생하였습니다.");
		}
	}

	public boolean checkUsername(CheckUsernameDto usernameDto) {
		Boolean check = userRepository.existsByUsername(usernameDto.getUsername()).orElseThrow(
			() -> new NullPointerException("아이디를 입력해 주세요.")
		);
		if (check) {
			throw new UsernameAlreadyExistsException("이미 존재하는 아이디 입니다.");
		} else {
			return true;
		}
	}

	public boolean checkNickname(CheckNicknameDto nicknameDto) {
		Boolean check = userRepository.existsByNickname(nicknameDto.getNickname()).orElseThrow(
			() -> new NullPointerException("닉네임을 입력해 주세요.")
		);
		if (check) {
			throw new NicknameAlreadyExistsException("이미 존재하는 닉네임 입니다.");
		} else {
			return true;
		}
	}

	public boolean checkSendSignupEmail(CheckEmailDto emailDto) {
		String redisCode = redisUtil.getData("Auth:" + emailDto.getEmail());
		if (redisCode == null || redisCode.isEmpty()) {
			throw new InvalidAuthCodeException("이메일에 대한 인증 요청이 존재하지 않거나 만료되었습니다.");
		}
		if (emailDto.getCode() == null || !emailDto.getCode().equals(redisCode)) {
			throw new InvalidAuthCodeException("인증번호가 일치하지 않습니다.");
		}
		if (userRepository.findIdByEmail(emailDto.getEmail()).isPresent()) {
			throw new EmailAlreadyRegisteredException("이 이메일로 이미 가입된 계정이 있습니다.");
		}
		return true;
	}

	public boolean sendSignupEmail(SendEmailDto emailDto) {
		String code = createCode();
		sendEmail(emailDto.getEmail(), "Play The Piano", setContext(code, "email-signup"), code,
			"Auth:");
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
			throw new UsernameDuplicateException("아이디 중복 체크를 확인해 주세요.");
		}
		if (requestDto.getNickname().isEmpty()) {
			throw new NicknameDuplicateException("닉네임 중복 체크를 확인해 주세요.");
		}
		if (!requestDto.getPassword().equals(requestDto.getCheckPassword())) {
			throw new PasswordMismatchException("두 비밀번호가 일치하지 않습니다.");
		}
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
		User user = new User(requestDto, encodedPassword);
		userRepository.createUser(user);
		return new SignupResponseDto(requestDto.getUsername(), requestDto.getNickname());
	}

	public LoginResponseDto login(LoginRequestDto requestDto) {
		User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
			() -> new UserNotFoundException("존재하지 않는 아이디 입니다."));
		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new UserNotFoundException("비밀번호가 다릅니다.");
		}
		return new LoginResponseDto(user.getId(), user.getUsername(), user.getNickname(),
			user.getRole());
	}

	public boolean sendFindUsernameEmail(SendEmailDto emailDto) {
		String code = createCode();
		sendEmail(emailDto.getEmail(), "Play The Piano", setContext(code, "email-findUsername"),
			code, "findUsername:");
		return true;
	}

	public String findUsernameByEmail(CheckEmailDto emailDto) {
		String redisCode = redisUtil.getData("findUsername:" + emailDto.getEmail());
		if (emailDto.getCode() == null) {
			throw new InvalidAuthCodeException("인증번호를 입력하지 않았습니다.");
		}
		if (redisCode == null || redisCode.isEmpty()) {
			throw new InvalidAuthCodeException("인증번호가 만료되었거나 존재하지 않습니다.");
		}
		if (!emailDto.getCode().equals(redisCode)) {
			throw new InvalidAuthCodeException("인증번호가 일치하지 않습니다.");
		}
		return userRepository.findUsernameByEmail(emailDto.getEmail()).orElseThrow(
			() -> new EmailAlreadyRegisteredException("이 이메일로 가입된 아이디가 없습니다.")
		);
	}

	public boolean sendFindPasswordEmail(SendEmailDto emailDto) {
		String code = createCode();
		sendEmail(emailDto.getEmail(), "Play The Piano", setContext(code, "email-findPassword"),
			code, "findPassword:");
		return true;
	}

	public boolean findPasswordByEmail(CheckEmailDto emailDto) {
		String redisCode = redisUtil.getData("findPassword:" + emailDto.getEmail());
		if (redisCode == null || redisCode.isEmpty()) {
			throw new InvalidAuthCodeException("이메일에 해당하는 인증 요청이 존재하지 않습니다. 인증을 다시 시도해주세요.");
		}
		if (emailDto.getCode() == null || !emailDto.getCode().equals(redisCode)) {
			throw new InvalidAuthCodeException("인증번호가 일치하지 않습니다.");
		}
		if (userRepository.findIdByEmail(emailDto.getEmail()).isEmpty()) {
			throw new EmailAlreadyRegisteredException("해당 이메일로 가입된 계정을 찾을 수 없습니다.");
		}
		return true;
	}

	@Transactional
	public boolean updatePassword(UpdatePasswordDto passwordDto) {
		Long id = userRepository.findIdByEmail(passwordDto.getEmail()).orElseThrow(
			() -> new InvalidAuthCodeException("유효하지 않은 이메일 입니다.")
		);
		if (!passwordDto.getPassword().equals(passwordDto.getCheckPassword())) {
			throw new PasswordMismatchException("두 비밀번호가 일치하지 않습니다.");
		}
		String encodedPassword = passwordEncoder.encode(passwordDto.getPassword());
		userRepository.updatePassword(id, encodedPassword);
		return true;
	}

	@Transactional
	public void updateNickname(User user, CheckNicknameDto nicknameDto) {
		String currentNickname = userRepository.getNickname(user.getId())
			.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 입니다."));
		if (currentNickname.equals(nicknameDto.getNickname())) {
			throw new NicknameDuplicateException("현재 닉네임과 같은 닉네임 입니다.");
		}
		checkNickname(nicknameDto);
		userRepository.updateNickname(user.getId(), nicknameDto.getNickname());
	}

	@Transactional
	public void updateUsername(User user, CheckUsernameDto usernameDto) {
		String currentUsername = userRepository.getUsername(user.getId())
			.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 입니다."));
		if (currentUsername.equals(usernameDto.getUsername())) {
			throw new UsernameDuplicateException("현재 아이디와 같은 아이디 입니다.");
		}
		checkUsername(usernameDto);
		userRepository.updateUsername(user.getId(), usernameDto.getUsername());
	}

	@Transactional
	public void createRoleChangeRequest(User user, String content) {
		if (user.getRole() != RoleEnum.CUSTOMER) {
			throw new RoleNotAllowedException("손님만 신청이 가능합니다.");
		}
		if (userRepository.getRoleChangeRequest(user.getId()).isPresent()) {
			throw new RoleNotAllowedException("이미 신청하셨습니다.");
		}
		userRepository.createRoleChangeRequest(new RoleChangeRequest(user, content));
	}

	@Transactional
	public void updateUserRole(User user, Long id) {
		if (user.getRole() != RoleEnum.ADMIN) {
			throw new RoleNotAllowedException("해당 기능을 위한 접근 권한이 없습니다.");
		}
		if (userRepository.getRoleChangeRequest(id).isEmpty()) {
			throw new UserNotFoundException("신청기록이 없는 유저입니다.");
		}
		userRepository.updateRoleStudent(id);
		userRepository.deleteRoleChangeRequest(id);
	}

	public MyPageResponseDto getMyPage(User user) {
		return userRepository.getMyPage(user.getId())
			.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 입니다."));
	}

	public List<RoleChangeResponseDto> getRoleChangeRequests(User user, int page, int size) {
		if (user.getRole() != RoleEnum.ADMIN) {
			throw new RoleNotAllowedException("해당 기능을 위한 접근 권한이 없습니다.");
		}
		return userRepository.getRoleChangeRequests(page, size);
	}

	public void sendDeleteAccountEmail(User user) {
		String code = createCode();
		String email = userRepository.getEmail(user.getId())
			.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 입니다."));
		sendEmail(email, "Play The Piano", setContext(code, "email-deleteAccount"),
			code, "deleteAccount:");
	}

	public boolean getRoleChangeRequest(User user){
		return userRepository.getRoleChangeRequest(user.getId()).isPresent();
	}

	@Transactional
	public void deleteAccount(User user,String code) {
		String email = userRepository.getEmail(user.getId())
			.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 입니다."));
		String redisCode = redisUtil.getData("deleteAccount:" + email);
		if (redisCode == null || redisCode.isEmpty()) {
			throw new InvalidAuthCodeException("이메일에 해당하는 인증 요청이 존재하지 않습니다. 인증을 다시 시도해주세요.");
		}
		if (code == null || !code.equals(redisCode)) {
			throw new InvalidAuthCodeException("인증번호가 일치하지 않습니다.");
		}
		userRepository.deleteUserToken(user.getId());
		userRepository.deleteUser(user.getId());
	}
}
