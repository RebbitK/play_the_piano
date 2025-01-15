package com.example.play_the_piano.quiz.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizListResponseDto {

	private List<QuizzesResponseDto> quizList;

	private int totalPage;

}
