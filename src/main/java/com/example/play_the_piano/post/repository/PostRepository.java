package com.example.play_the_piano.post.repository;

import com.example.play_the_piano.post.dto.GetPostResponseDto;
import com.example.play_the_piano.post.dto.PostThumbnailDto;
import com.example.play_the_piano.post.dto.PostUpdateRequestDto;
import com.example.play_the_piano.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostRepository {

	void createPost(Post post);

	List<PostThumbnailDto> getPostsByCategory(String category, int offset, int limit);

	int getTotalPostsCountByCategory(String category);

	Optional<GetPostResponseDto> getPostDtoById(Long id);

	Optional<Post> getPostById(Long id);

	void updatePostViewCount(Long id);

	void updateContent(@Param("id") Long id, @Param("content") String content);

	void deletePost(Long id);

	void updatePost(PostUpdateRequestDto requestDto);
}
