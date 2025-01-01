package com.example.play_the_piano.post.repository;

import com.example.play_the_piano.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostRepository {
	void createPost(Post post);

	void updateContent(Post post);
}
