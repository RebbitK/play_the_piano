package com.example.play_the_piano.quiz.repository;

import com.example.play_the_piano.quiz.dto.AnswerQuizRequestDto;
import com.example.play_the_piano.quiz.dto.AnswerQuizResponseDto;
import com.example.play_the_piano.quiz.dto.LoadQuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizSearchRequestDto;
import com.example.play_the_piano.quiz.dto.QuizzesResponseDto;
import com.example.play_the_piano.quiz.entity.CompleteQuiz;
import com.example.play_the_piano.quiz.entity.Quiz;
import com.example.play_the_piano.quiz.entity.QuizLevel;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuizRepository {

	void createQuiz(Quiz quiz);

	void createCompleteQuiz(CompleteQuiz quiz);

	void updateContent(@Param("id") Long id, @Param("content") String content);

	List<QuizzesResponseDto> getQuizzesByQuizLevel(@Param("quizLevel") String quizLevel,
		@Param("offset") int offset, @Param("limit") int limit);

	Optional<QuizResponseDto> getQuizById(Long id);

	int getTotalQuizzesCountByQuizLevel(@Param("quizLevel") String quizLevel);

	List<LoadQuizResponseDto> loadQuizzesByQuizEnum(@Param("quizLevel") String quizLevel);

	Optional<AnswerQuizResponseDto> checkAnswer(AnswerQuizRequestDto requestDto);

	List<Long> getCompleteQuizzes(@Param("id") Long id,
		@Param("quizLevel") QuizLevel quizLevel);

	boolean getCompleteQuiz(@Param("userId") Long userId,
		@Param("quizId") Long quizId);

	Optional<Long> getNextQuiz(QuizSearchRequestDto requestDto);

	Optional<Long> getPreviousQuiz(QuizSearchRequestDto requestDto);

}
