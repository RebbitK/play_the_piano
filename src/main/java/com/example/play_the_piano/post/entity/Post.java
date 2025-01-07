package com.example.play_the_piano.post.entity;

import com.example.play_the_piano.global.entity.Deleted;
import com.example.play_the_piano.global.entity.TimeStamped;
import com.example.play_the_piano.post.dto.PostRequestDto;
import com.example.play_the_piano.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Post extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String content;

	@Enumerated(EnumType.STRING)
	private PostEnum category;

	private Integer viewCount;

	@Enumerated(EnumType.STRING)
	private Deleted deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	public Post(PostRequestDto requestDto, User user) {
		this.title = requestDto.getTitle();
		this.category = requestDto.getCategory();
		this.content = requestDto.getContent();
		this.user = user;
		deleted = Deleted.UNDELETE;
	}

	public void updateContent(String content) {
		this.content = content;
	}

}
