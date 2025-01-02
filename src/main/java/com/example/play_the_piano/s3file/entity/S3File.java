package com.example.play_the_piano.s3file.entity;

import com.example.play_the_piano.global.entity.TimeStamped;
import com.example.play_the_piano.post.entity.Post;
import jakarta.persistence.Entity;
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
public class S3File extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String url;

	@ManyToOne
	private Post post;

	public S3File(String url, Post port) {
		this.url = url;
		this.post = port;
	}

}