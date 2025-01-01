package com.example.play_the_piano.post.controller;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.post.dto.PostRequestDto;
import com.example.play_the_piano.post.service.PostService;
import com.example.play_the_piano.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {

	private final PostService postService;

	@PostMapping
	private ResponseEntity<CommonResponse<String>> createPost(
		@RequestBody PostRequestDto postRequestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		postService.createPost(postRequestDto, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<String>builder()
				.msg("createPost successful")
				.data("성공")
				.build());
	}
}

