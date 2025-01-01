package com.example.play_the_piano.post.dto;

import com.example.play_the_piano.post.entity.PostEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Getter
@Setter
public class PostRequestDto {

	private String title;

	private String content;

	@Enumerated(EnumType.STRING)
	private PostEnum category;

	private List<MultipartFile> files;
}
