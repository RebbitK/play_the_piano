package com.example.play_the_piano.quiz.controller;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.quiz.dto.AnswerQuizRequestDto;
import com.example.play_the_piano.quiz.dto.AnswerQuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizListResponseDto;
import com.example.play_the_piano.quiz.dto.QuizRequestDto;
import com.example.play_the_piano.quiz.entity.QuizLevel;
import com.example.play_the_piano.quiz.service.QuizService;
import com.example.play_the_piano.user.entity.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/quizzes")
@Slf4j
public class QuizController {

	private final QuizService quizService;

	@PostMapping
	public ResponseEntity<CommonResponse<Long>> createQuiz(@RequestBody QuizRequestDto quizRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long id = quizService.createQuiz(quizRequestDto,userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Long>builder()
				.msg("create quiz successful")
				.data(id)
				.build());
	}

	@GetMapping
	public ResponseEntity<CommonResponse<QuizListResponseDto>> getQuizzes(@RequestParam(name = "quizLevel")
	QuizLevel quizLevel,
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "10") int size,
		@AuthenticationPrincipal UserDetailsImpl userDetails){
		QuizListResponseDto responseDto = quizService.getQuizzes(userDetails.getUser(), quizLevel,page,size);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<QuizListResponseDto>builder()
				.msg("get quizzes successful")
				.data(responseDto)
				.build());
	}

	@PostMapping("/answer")
	public ResponseEntity<CommonResponse<List<AnswerQuizResponseDto>>> answerQuiz(@RequestBody List<AnswerQuizRequestDto> requestDtos,@AuthenticationPrincipal UserDetailsImpl userDetails){
		List<AnswerQuizResponseDto> responseDtos = quizService.answerQuiz(userDetails.getUser(),requestDtos);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<List<AnswerQuizResponseDto>>builder()
				.msg("quizAnswer successful")
				.data(responseDtos)
				.build());
	}

}
