package com.example.play_the_piano.post.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostThumbnailDto {

	private String title;

	private String thumbnailUrl;

	private LocalDateTime createdAt;

}
