package com.example.play_the_piano.quiz.service;

import com.example.play_the_piano.global.exception.custom.QuizNotFoundException;
import com.example.play_the_piano.global.exception.custom.RoleNotAllowedException;
import com.example.play_the_piano.global.exception.custom.UserNotFoundException;
import com.example.play_the_piano.quiz.dto.AnswerQuizRequestDto;
import com.example.play_the_piano.quiz.dto.AnswerQuizResponseDto;
import com.example.play_the_piano.quiz.dto.CompleteQuizResponseDto;
import com.example.play_the_piano.quiz.dto.LoadQuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizListResponseDto;
import com.example.play_the_piano.quiz.dto.QuizRequestDto;
import com.example.play_the_piano.quiz.dto.QuizResponseDto;
import com.example.play_the_piano.quiz.entity.CompleteQuiz;
import com.example.play_the_piano.quiz.entity.Quiz;
import com.example.play_the_piano.quiz.entity.QuizLevel;
import com.example.play_the_piano.quiz.repository.QuizRepository;
import com.example.play_the_piano.s3file.entity.ObjectEnum;
import com.example.play_the_piano.s3file.service.S3FileService;
import com.example.play_the_piano.user.entity.RoleEnum;
import com.example.play_the_piano.user.entity.User;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

	private final S3FileService fileService;

	private final QuizRepository quizRepository;

	@Transactional
	public Long createQuiz(QuizRequestDto requestDto, User user) {
		checkAdmin(user);
		List<String> base64Images = fileService.extractBase64ImagesFromContent(
			requestDto.getContent());
		String content = requestDto.getContent();
		if (!base64Images.isEmpty()) {
			requestDto.setContent("");
		}
		Quiz quiz = new Quiz(requestDto, user);
		quizRepository.createQuiz(quiz);
		if (!base64Images.isEmpty()) {
			String updatedContent = fileService.decodeText(base64Images, content, ObjectEnum.QUIZ,
				quiz.getId());
			quiz.updateContent(updatedContent);
			quizRepository.updateContent(quiz.getId(), quiz.getContent());
		}
		return quiz.getId();
	}

	public QuizListResponseDto getQuizzes(User user, QuizLevel quizLevel, int page, int size) {
		checkStudent(user);
		int offset = (page - 1) * size;
		int totalPage = (quizRepository.getTotalQuizzesCountByQuizLevel(quizLevel.name())-1) / size + 1;
		return new QuizListResponseDto(
			quizRepository.getQuizzesByQuizLevel(quizLevel.name(), offset, size), totalPage);
	}


	public QuizResponseDto getQuiz(User user, Long id) {
		checkStudent(user);
		QuizResponseDto responseDto = quizRepository.getQuizById(id).orElseThrow(() ->
			new QuizNotFoundException("해당 퀴즈가 존재하지 않습니다."));
		if(user.getRole()!=RoleEnum.ADMIN) {
			responseDto.setAnswer("");
		}
		return responseDto;
	}

	public List<LoadQuizResponseDto> randomQuiz(User user, QuizLevel quizLevel, int count) {
		checkStudent(user);
		int totalQuiz = quizRepository.getTotalQuizzesCountByQuizLevel(quizLevel.name());
		if (totalQuiz == 0) {
			throw new QuizNotFoundException("현재 해당 난이도의 퀴즈가 존재하지 않습니다.");
		} else if (totalQuiz < count) {
			List<LoadQuizResponseDto> responseDtos = quizRepository.loadQuizzesByQuizEnum(
				quizLevel.name());
			Collections.shuffle(responseDtos);
			return responseDtos;
		}
		List<LoadQuizResponseDto> responseDtos = quizRepository.loadQuizzesByQuizEnum(
			quizLevel.name());
		Collections.shuffle(responseDtos);
		return responseDtos.subList(0, count);
	}

	@Transactional
	public AnswerQuizResponseDto answerQuiz(User user, AnswerQuizRequestDto requestDto) {
		checkStudent(user);
		AnswerQuizResponseDto responseDto = quizRepository.checkAnswer(requestDto)
			.orElseThrow(() -> new QuizNotFoundException("해당 퀴즈가 존재하지 않습니다."));
		if(responseDto.isCorrect()){
			quizRepository.createCompleteQuiz(new CompleteQuiz(new Quiz(responseDto.getId()),user));
		}
		return responseDto;
	}

	public List<CompleteQuizResponseDto> getCompleteQuizzes(User user,QuizLevel quizLevel) {
		checkStudent(user);
		return quizRepository.getCompleteQuizzes(user.getId(),quizLevel);
	}

	public boolean getCompleteQuiz(User user,Long quizId) {
		checkStudent(user);
		return quizRepository.getCompleteQuiz(user.getId(),quizId);
	}

	private void checkAdmin(User user) {
		if (user.getRole() == null) {
			throw new UserNotFoundException("잘못된 접근 입니다.");
		}
		if (user.getRole() != RoleEnum.ADMIN) {
			throw new RoleNotAllowedException("해당 기능을 위한 접근 권한이 없습니다.");
		}
	}


	private void checkStudent(User user) {
		if (user.getRole() == null) {
			throw new UserNotFoundException("잘못된 접근 입니다.");
		}
		if (!(user.getRole() == RoleEnum.ADMIN || user.getRole() == RoleEnum.STUDENT)) {
			throw new RoleNotAllowedException("해당 기능을 위한 접근 권한이 없습니다.");
		}
	}
}
