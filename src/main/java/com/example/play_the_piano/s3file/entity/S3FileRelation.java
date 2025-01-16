package com.example.play_the_piano.s3file.entity;

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
public class S3FileRelation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private S3File file;

	@Enumerated(EnumType.STRING)
	private ObjectEnum objectEnum;

	private Long objectId;

	public S3FileRelation(S3File file, ObjectEnum objectEnum, Long objectId) {
		this.file = file;
		this.objectEnum = objectEnum;
		this.objectId = objectId;
	}
}
