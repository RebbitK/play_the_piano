package com.example.play_the_piano.post.service;

import com.example.play_the_piano.global.exception.custom.Base64ConversionException;
import com.example.play_the_piano.global.exception.custom.InvalidBase64ExceptionException;
import com.example.play_the_piano.global.exception.custom.PostNotFoundException;
import com.example.play_the_piano.post.dto.GetPostResponseDto;
import com.example.play_the_piano.post.dto.GetPostsResponseDto;
import com.example.play_the_piano.post.dto.PostRequestDto;
import com.example.play_the_piano.post.dto.PostThumbnailDto;
import com.example.play_the_piano.post.entity.Post;
import com.example.play_the_piano.post.entity.PostEnum;
import com.example.play_the_piano.post.repository.PostRepository;
import com.example.play_the_piano.s3file.customMultipartFile.CustomMultipartFile;
import com.example.play_the_piano.s3file.service.S3FileService;
import com.example.play_the_piano.user.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final S3FileService fileService;

	private final PostRepository postRepository;

	@Transactional
	@CacheEvict(value = "posts", allEntries = true)
	public void createPost(PostRequestDto postRequestDto, User user) {
		List<String> base64Images = extractBase64ImagesFromContent(postRequestDto.getContent());
		Post post = new Post(postRequestDto,user);
		if (!base64Images.isEmpty()) {
			Map<String, String> base64ToUrlMap = new HashMap<>();

			for (String base64Image : base64Images) {
				MultipartFile file = convertBase64ToMultipartFile(base64Image);
				String uploadedImageUrl = fileService.upload(file, "images/", post);
				base64ToUrlMap.put(base64Image, uploadedImageUrl);
			}

			String updatedContent = updateContentWithImageUrls(postRequestDto.getContent(),
				base64ToUrlMap);
			post.updateContent(updatedContent);
		}
		if (postRequestDto.getFiles() != null && !postRequestDto.getFiles().isEmpty()) {
			for (MultipartFile file : postRequestDto.getFiles()) {
				fileService.upload(file, "files/", post);
			}
		}
		postRepository.createPost(post);
	}

	@Cacheable(value = "posts", key = "#category.name() + '_' + #page + '_' + #size", condition = "#size > 0")
	public GetPostsResponseDto getPosts(PostEnum category, int page, int size) {
		int offset = (page - 1) * size;
		int totalPage = postRepository.getTotalPostsCountByCategory(category.name()) / size + 1;
		List<PostThumbnailDto> posts = postRepository.getPostsByCategory(category.name(), offset, size);
		List<String> titles = posts.stream().map(PostThumbnailDto::getTitle).toList();
		List<String> thumbnails = posts.stream().map(PostThumbnailDto::getThumbnailUrl).toList();
		List<LocalDateTime> createdAts = posts.stream().map(PostThumbnailDto::getCreatedAt).toList();
		
		return new GetPostsResponseDto(thumbnails, titles, totalPage, createdAts);
	}

	@Cacheable(value = "post", key = "#id")
	public GetPostResponseDto getPost(Long id) {
		return postRepository.getPostById(id)
			.orElseThrow(() -> new PostNotFoundException("해당 포스트가 존재하지 않습니다."));
	}

	private List<String> extractBase64ImagesFromContent(String content) {
		String regex = "data:image/(png|jpeg|gif|bmp|svg\\+xml);base64,([A-Za-z0-9+/=]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		List<String> base64Images = new ArrayList<>();
		while (matcher.find()) {
			base64Images.add(matcher.group(0));
		}
		return base64Images;
	}

	private String updateContentWithImageUrls(String content, Map<String, String> base64ToUrlMap) {
		for (Map.Entry<String, String> entry : base64ToUrlMap.entrySet()) {
			content = content.replace(entry.getKey(), entry.getValue());
		}
		return content;
	}

	private MultipartFile convertBase64ToMultipartFile(String base64Image) {
		String[] parts = base64Image.split(",");
		if (parts.length != 2) {
			throw new InvalidBase64ExceptionException("잘못된 Base64 문자열입니다.");
		}
		String base64Data = parts[1];
		String metadata = parts[0];
		String extension = metadata.split(";")[0].split("/")[1];
		byte[] imageBytes = Base64.decodeBase64(base64Data);

		try {
			String filename = UUID.randomUUID().toString() + "." + extension;
			return new CustomMultipartFile(imageBytes, filename);
		} catch (Exception e) {
			throw new Base64ConversionException("Base64를 MultipartFile로 변환하는 중 오류 발생했습니다.");
		}
	}
}
