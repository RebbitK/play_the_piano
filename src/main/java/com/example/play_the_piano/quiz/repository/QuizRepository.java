package com.example.play_the_piano.quiz.repository;

import com.example.play_the_piano.quiz.dto.QuizResponseDto;
import com.example.play_the_piano.quiz.dto.QuizzesResponseDto;
import com.example.play_the_piano.quiz.entity.Quiz;
import com.example.play_the_piano.quiz.entity.QuizEnum;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuizRepository {

	void createQuiz(Quiz quiz);

	void updateContent(@Param("id") Long id, @Param("content") String content);

	List<QuizzesResponseDto> getQuizzesByQuizEnum(@Param("quizEnum") String quizEnum, @Param("offset") int offset, @Param("limit") int limit);

	Optional<QuizResponseDto> getQuizById(Long id);

	int getTotalQuizzesCountByQuizEnum(@Param("quizEnum") String quizEnum);



}
