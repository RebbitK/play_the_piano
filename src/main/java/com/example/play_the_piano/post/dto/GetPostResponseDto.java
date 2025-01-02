package com.example.play_the_piano.post.dto;

import com.example.play_the_piano.post.entity.PostEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetPostResponseDto {

	private String title;

	private String content;

	@Enumerated(EnumType.STRING)
	private PostEnum category;

	private String nickname;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;
}
