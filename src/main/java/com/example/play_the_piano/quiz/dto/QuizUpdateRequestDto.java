package com.example.play_the_piano.quiz.dto;

import com.example.play_the_piano.quiz.entity.QuizLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class QuizUpdateRequestDto {

	private Long id;

	private String title;

	private String content;

	private String answer;

	@Enumerated(EnumType.STRING)
	private QuizLevel quizLevel;

}
