package com.example.play_the_piano.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPostsResponseDto {

	private List<String> thumbnails;

	private List<String> titles;

	private int totalPosts;

	private List<LocalDateTime> createdAts;

}
