package com.example.play_the_piano.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@EnableRedisRepositories
@EnableCaching
@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	private static final String REDISSON_HOST_PREFIX = "redis://";

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
		return Redisson.create(config);
	}

	// 연결정보
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	// ObjectMapper 등록 (JavaTimeModule 포함)
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());  // LocalDateTime 직렬화 및 역직렬화 가능하게 설정
		objectMapper.findAndRegisterModules(); // 모든 모듈 자동 등록
		return objectMapper;
	}

	// 직렬화 / 역직렬화
	@Bean
	public RedisTemplate<String, Object> redisTemplate(ObjectMapper objectMapper) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());

		// GenericJackson2JsonRedisSerializer로 값 직렬화 설정
		GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // Value 직렬화 방식 설정
		return redisTemplate;
	}

	// 캐시 매니저
	@Bean
	public CacheManager cacheManager(ObjectMapper objectMapper) {
		// RedisSerializationContext에서 직렬화 설정
		RedisSerializationContext.SerializationPair<Object> pair =
			RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

		RedisCacheManager.RedisCacheManagerBuilder builder =
			RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory());

		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(pair)  // Value Serializer 변경
			.disableCachingNullValues()
			.entryTtl(Duration.ofMinutes(10L));

		builder.cacheDefaults(configuration);

		return builder.build();
	}
}
