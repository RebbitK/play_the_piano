package com.example.play_the_piano.s3file.entity;

import com.example.play_the_piano.global.entity.Deleted;
import com.example.play_the_piano.global.entity.TimeStamped;
import com.example.play_the_piano.post.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	private Deleted deleted;

	@Enumerated(EnumType.STRING)
	private TypeEnum typeEnum;

	public S3File(String url, Post port,TypeEnum typeEnum) {
		this.url = url;
		this.post = port;
		deleted = Deleted.UNDELETE;
		this.typeEnum = typeEnum;
	}

}