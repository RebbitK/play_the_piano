package com.example.play_the_piano.post.controller;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.post.dto.GetPostResponseDto;
import com.example.play_the_piano.post.dto.GetPostsResponseDto;
import com.example.play_the_piano.post.dto.PostRequestDto;
import com.example.play_the_piano.post.entity.Post;
import com.example.play_the_piano.post.entity.PostEnum;
import com.example.play_the_piano.post.service.PostService;
import com.example.play_the_piano.user.entity.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<CommonResponse<Long>> createPost(
		@RequestBody PostRequestDto postRequestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long id = postService.createPost(postRequestDto, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Long>builder()
				.msg("createPost successful")
				.data(id)
				.build());
	}

	@PostMapping("/files")
	public ResponseEntity<CommonResponse<Void>> uploadFile(@RequestParam("file") MultipartFile file,
		@RequestParam("postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		postService.uploadPostFile(file, postId, userDetails.getUser().getId());
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<Void>builder()
				.msg("createPost successful")
				.build());
	}

	@GetMapping
	public ResponseEntity<CommonResponse<GetPostsResponseDto>> getPosts(
		@RequestParam(name = "category") PostEnum category,
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "10") int size) {
		GetPostsResponseDto responseDto = postService.getPosts(category, page, size);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<GetPostsResponseDto>builder()
				.msg("view Posts successful")
				.data(responseDto)
				.build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CommonResponse<GetPostResponseDto>> getPost(@PathVariable Long id) {
		GetPostResponseDto responseDto = postService.getPost(id);
		return ResponseEntity.status(HttpStatus.OK.value())
			.body(CommonResponse.<GetPostResponseDto>builder()
				.msg("view Post successful")
				.data(responseDto)
				.build());
	}

	@GetMapping("/test")
	public ResponseEntity<?> testLocalDateTime() {
		return ResponseEntity.ok(LocalDateTime.now());
	}
}

