package com.example.play_the_piano.quiz.controller;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.quiz.dto.AdminQuizResponseDto;
import com.example.play_the_piano.quiz.dto.AnswerQuizRequestDto;
import com.example.play_the_piano.quiz.dto.AnswerQuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizListResponseDto;
import com.example.play_the_piano.quiz.dto.QuizRequestDto;
import com.example.play_the_piano.quiz.dto.QuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizSearchRequestDto;
import com.example.play_the_piano.quiz.dto.QuizUpdateRequestDto;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping()
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

	@GetMapping("/{id}")
	public ResponseEntity<CommonResponse<QuizResponseDto>> getQuiz(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		QuizResponseDto responseDto = quizService.getQuiz(userDetails.getUser(),id);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<QuizResponseDto>builder()
				.msg("get quiz successful")
				.data(responseDto)
				.build());
	}

	@GetMapping("/admin/{id}")
	public ResponseEntity<CommonResponse<AdminQuizResponseDto>> getAdminQuiz(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
		AdminQuizResponseDto responseDto = quizService.getAdminQuiz(userDetails.getUser(),id);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<AdminQuizResponseDto>builder()
				.msg("get adminQuiz successful")
				.data(responseDto)
				.build());
	}

	@GetMapping("/next")
	public ResponseEntity<CommonResponse<Long>> getNextQuiz(@RequestParam(name = "quizLevel")
	QuizLevel quizLevel, @RequestParam(name = "quizId") Long quizId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long id = quizService.getNextQuiz(userDetails.getUser(),new QuizSearchRequestDto(quizId,quizLevel));
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Long>builder()
				.msg("get nextQuiz successful")
				.data(id)
				.build());
	}

	@GetMapping("/previous")
	public ResponseEntity<CommonResponse<Long>> getPreviousQuiz(@RequestParam(name = "quizLevel")
	QuizLevel quizLevel, @RequestParam(name = "quizId") Long quizId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long id = quizService.getPreviousQuiz(userDetails.getUser(),new QuizSearchRequestDto(quizId,quizLevel));
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Long>builder()
				.msg("get previousQuiz successful")
				.data(id)
				.build());
	}


	@PostMapping("/answer")
	public ResponseEntity<CommonResponse<AnswerQuizResponseDto>> answerQuiz(@RequestBody AnswerQuizRequestDto requestDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
		AnswerQuizResponseDto responseDtos = quizService.answerQuiz(userDetails.getUser(),requestDto);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<AnswerQuizResponseDto>builder()
				.msg("quizAnswer successful")
				.data(responseDtos)
				.build());
	}

	@GetMapping("/complete-quizzes")
	public ResponseEntity<CommonResponse<List<Long>>> getCompleteQuizzes(@RequestParam(name = "quizLevel")
	QuizLevel quizLevel,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		List<Long> responseDtos = quizService.getCompleteQuizzes(userDetails.getUser(),quizLevel);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<List<Long>>builder()
				.msg("get CompleteQuizzes successful")
				.data(responseDtos)
				.build());
	}

	@GetMapping("/complete-quiz/{id}")
	public ResponseEntity<CommonResponse<Boolean>> getCompleteQuiz(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Boolean responseDto = quizService.getCompleteQuiz(userDetails.getUser(),id);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Boolean>builder()
				.msg("get CompleteQuiz successful")
				.data(responseDto)
				.build());
	}

	@PatchMapping("/update")
	public ResponseEntity<CommonResponse<Void>> updateQuiz(@RequestBody QuizUpdateRequestDto requestDto,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		quizService.updateQuiz(requestDto,userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("update Quiz successful")
				.build());
	}

	@PatchMapping("/delete/{id}")
	public ResponseEntity<CommonResponse<Void>> softDeleteQuiz(@PathVariable Long id,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		quizService.softDeleteQuiz(userDetails.getUser(),id);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("delete quiz successful")
				.build());
	}

}
