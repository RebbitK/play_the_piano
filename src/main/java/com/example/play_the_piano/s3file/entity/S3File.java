package com.example.play_the_piano.s3file.entity;

import com.example.play_the_piano.global.entity.Deleted;
import com.example.play_the_piano.global.entity.TimeStamped;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Enumerated(EnumType.STRING)
	private Deleted deleted;

	@Enumerated(EnumType.STRING)
	private TypeEnum typeEnum;

	public S3File(String url, TypeEnum typeEnum) {
		this.url = url;
		deleted = Deleted.UNDELETE;
		this.typeEnum = typeEnum;
	}

}