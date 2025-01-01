package com.example.play_the_piano.post.service;

import com.example.play_the_piano.global.exception.custom.Base64ConversionException;
import com.example.play_the_piano.global.exception.custom.InvalidBase64ExceptionException;
import com.example.play_the_piano.post.dto.PostRequestDto;
import com.example.play_the_piano.post.entity.Post;
import com.example.play_the_piano.post.repository.PostRepository;
import com.example.play_the_piano.s3file.customMultipartFile.CustomMultipartFile;
import com.example.play_the_piano.s3file.service.S3FileService;
import com.example.play_the_piano.user.entity.User;
import jakarta.transaction.Transactional;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final S3FileService fileService;

	private final PostRepository postRepository;

	@Transactional
	public void createPost(PostRequestDto postRequestDto, User user) {
		List<String> base64Images = extractBase64ImagesFromContent(postRequestDto.getContent());
		Post post = new Post(postRequestDto,user);
		postRepository.createPost(post);
		if (!base64Images.isEmpty()) {
			Map<String, String> base64ToUrlMap = new HashMap<>();

			for (String base64Image : base64Images) {
				MultipartFile file = convertBase64ToMultipartFile(base64Image);
				String uploadedImageUrl = fileService.upload(file, "images/",post);
				base64ToUrlMap.put(base64Image, uploadedImageUrl);
			}

			String updatedContent = updateContentWithImageUrls(postRequestDto.getContent(), base64ToUrlMap);
			postRequestDto.setContent(updatedContent);
			postRepository.updateContent(post);
		}
		post.updateContent(postRequestDto.getContent());
		postRepository.updateContent(post);
		if (postRequestDto.getFiles() != null && !postRequestDto.getFiles().isEmpty()){
			for(MultipartFile file : postRequestDto.getFiles()) {
				String uploadFileUrl = fileService.upload(file, "files/",post);
			}
		}
	}

	private List<String> extractBase64ImagesFromContent(String content) {
		String regex = "data:image/([a-zA-Z]*);base64,([^\"]*)";
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

	public MultipartFile convertBase64ToMultipartFile(String base64Image) {
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
			return new CustomMultipartFile(imageBytes,filename);
		} catch (Exception e) {
			throw new Base64ConversionException("Base64를 MultipartFile로 변환하는 중 오류 발생했습니다.");
		}
	}
}
