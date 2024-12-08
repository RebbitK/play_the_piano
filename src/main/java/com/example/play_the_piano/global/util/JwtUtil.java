package com.example.play_the_piano.global.util;

import com.example.play_the_piano.refreshToken.entity.RefreshToken;
import com.example.play_the_piano.refreshToken.repository.RefreshTokenRepository;
import com.example.play_the_piano.user.entity.RoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	// Header KEY 값
	public static final String AUTHORIZATION_HEADER = "Authorization";

	// Token 식별자
	public static final String BEARER_PREFIX = "Bearer ";

	@Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
	private String secretKey;

	private final long REFRESHTOKENTIME = 60 * 60 * 1000 * 24 * 7L;

	private final RefreshTokenRepository refreshTokenRepository;


	private SecretKey key;

	@PostConstruct
	public void init() {
		byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
		key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String validateRefreshToken(Long userId) {
		RefreshToken token = refreshTokenRepository.findByUserId(userId).orElseThrow(
			() -> new IllegalArgumentException("RefreshToken 이 유효하지 않습니다.")
		);
		String refreshToken = token.getRefreshToken().substring(7);
		Claims info = Jwts.parser().verifyWith(key).build()
			.parseSignedClaims(refreshToken).getPayload();
		RoleEnum roleEnum = RoleEnum.valueOf(info.get("role", String.class));
		return createAccessToken(info.get("userId", Long.class),
			info.get("username", String.class), roleEnum);
	}

	public void deleteRefreshToken(Long userId) {
		Optional<RefreshToken> checkToken = refreshTokenRepository.findByUserId(userId);
		checkToken.ifPresent(refreshTokenRepository::deleteRefreshToken);
	}

	public Claims getUserInfoFromToken(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	public String createToken(Long userId, String username, RoleEnum role) {
		Date date = new Date();

		String accessToken = createAccessToken(userId, username, role);
		deleteRefreshToken(userId);

		String refreshToken = BEARER_PREFIX +
			Jwts.builder()
				.claim("userId", userId)
				.claim("username", username)
				.claim("role", role)
				.setIssuedAt(new Date(date.getTime())) // 토큰 발행 시간 정보
				.setExpiration(new Date(date.getTime() + REFRESHTOKENTIME)) // set Expire Time
				.signWith(key)
				.compact();
		RefreshToken token = RefreshToken.builder().refreshToken(refreshToken).userId(userId)
			.build();
		refreshTokenRepository.insertRefreshToken(token);
		return accessToken;
	}

	public String createAccessToken(Long userId, String username, RoleEnum role) {
		Date date = new Date();

		long TOKEN_TIME = 60 * 60 * 10000;
		return BEARER_PREFIX +
			Jwts.builder()
				.claim("userId", userId)
				.claim("username", username)
				.claim("role", role)
				.setExpiration(new Date(date.getTime() + TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key)
				.compact();
	}

	public Claims getMemberInfoFromExpiredToken(String token) {
		try {
			return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}