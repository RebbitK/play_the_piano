package com.example.play_the_piano.refreshToken.repository;

import com.example.play_the_piano.refreshToken.entity.RefreshToken;
import com.example.play_the_piano.user.entity.User;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenRepository  {

	Optional<RefreshToken> findByUserId(Long userId);

	void insertRefreshToken(RefreshToken refreshToken);

	void deleteRefreshToken(RefreshToken refreshToken);

}