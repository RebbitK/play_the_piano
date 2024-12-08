package com.example.play_the_piano.refreshToken.repository;

import com.example.play_the_piano.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenRepository  {

	User findByUsername(String username);

}