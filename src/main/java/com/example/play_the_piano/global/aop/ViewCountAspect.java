package com.example.play_the_piano.global.aop;

import com.example.play_the_piano.post.service.PostService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ViewCountAspect {

	private final PostService postService;

	public ViewCountAspect(PostService postService) {
		this.postService = postService;
	}

	@Pointcut("execution(* com.example.play_the_piano.post.controller.PostController.getPost(..))")
	public void viewPostPointcut() {}

	@AfterReturning(value = "viewPostPointcut() && args(postId, ..)", argNames = "postId")
	public void incrementViewCount(Long postId) {
		postService.incrementViewCount(postId);
	}
}