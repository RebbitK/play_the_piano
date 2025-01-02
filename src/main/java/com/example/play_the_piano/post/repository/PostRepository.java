package com.example.play_the_piano.post.repository;

import com.example.play_the_piano.post.dto.GetPostResponseDto;
import com.example.play_the_piano.post.dto.PostThumbnailDto;
import com.example.play_the_piano.post.entity.Post;
import com.example.play_the_piano.post.entity.PostEnum;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostRepository {

	void createPost(Post post);

	List<PostThumbnailDto> getPostsByCategory(String category, int offset, int limit);

	int getTotalPostsCountByCategory(String category);

	Optional<GetPostResponseDto> getPostById(Long id);
}
