package com.example.play_the_piano.quiz.entity;

import com.example.play_the_piano.global.entity.TimeStamped;
import com.example.play_the_piano.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CompleteQuiz extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Quiz quiz;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	public CompleteQuiz(Quiz quiz, User user) {
		this.quiz = quiz;
		this.user = user;
	}

}
