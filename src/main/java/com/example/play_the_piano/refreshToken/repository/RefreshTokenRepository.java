package com.example.play_the_piano.refreshToken.repository;

import com.example.play_the_piano.refreshToken.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

	Optional<RefreshToken> findByUserId(Long userId);

}