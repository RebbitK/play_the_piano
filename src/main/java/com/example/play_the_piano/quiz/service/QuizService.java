package com.example.play_the_piano.quiz.service;

import com.example.play_the_piano.global.exception.custom.QuizNotFoundException;
import com.example.play_the_piano.global.exception.custom.RoleNotAllowedException;
import com.example.play_the_piano.global.exception.custom.UserNotFoundException;
import com.example.play_the_piano.quiz.dto.QuizListResponseDto;
import com.example.play_the_piano.quiz.dto.QuizRequestDto;
import com.example.play_the_piano.quiz.dto.QuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizzesResponseDto;
import com.example.play_the_piano.quiz.entity.Quiz;
import com.example.play_the_piano.quiz.entity.QuizEnum;
import com.example.play_the_piano.quiz.repository.QuizRepository;
import com.example.play_the_piano.s3file.entity.TypeEnum;
import com.example.play_the_piano.s3file.service.S3FileService;
import com.example.play_the_piano.user.entity.RoleEnum;
import com.example.play_the_piano.user.entity.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
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
			Map<String, String> base64ToUrlMap = new HashMap<>();

			for (String base64Image : base64Images) {
				MultipartFile file = fileService.convertBase64ToMultipartFile(base64Image);
				String uploadedImageUrl = fileService.uploadQuizFile(file, "images/", quiz,
					TypeEnum.IMAGE);
				base64ToUrlMap.put(base64Image, uploadedImageUrl);
			}

			String updatedContent = fileService.updateContentWithImageUrls(content,
				base64ToUrlMap);
			quiz.updateContent(updatedContent);
			quizRepository.updateContent(quiz.getId(), quiz.getContent());
		}
		return quiz.getId();
	}

	public QuizListResponseDto getQuizzes(User user, QuizEnum quizEnum, int page, int size){
		checkAdmin(user);
		int offset = (page - 1) * size;
		int totalPage = quizRepository.getTotalQuizzesCountByQuizEnum(quizEnum.name()) / size + 1;
		return new QuizListResponseDto(quizRepository.getQuizzesByQuizEnum(quizEnum.name(),offset,size),totalPage);
	}

	public QuizResponseDto getQuiz(User user,Long id){
		checkAdmin(user);
		return quizRepository.getQuizById(id).orElseThrow(()->
			new QuizNotFoundException("해당 퀴즈가 존재하지 않습니다."));
	}


	private void checkAdmin(User user) {
		if (user.getRole() == null) {
			throw new UserNotFoundException("잘못된 접근 입니다.");
		}
		if (user.getRole() != RoleEnum.ADMIN) {
			throw new RoleNotAllowedException("해당 기능을 위한 접근 권한이 없습니다.");
		}
	}
}
