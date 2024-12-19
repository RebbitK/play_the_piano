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
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

	// 직렬화 / 역직렬화
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		return redisTemplate;
	}

	// 캐시 매니저
	@Bean
	public CacheManager cacheManager() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer =
			new GenericJackson2JsonRedisSerializer(objectMapper);

		RedisCacheManager.RedisCacheManagerBuilder builder =
			RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(
				redisConnectionFactory());

		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					genericJackson2JsonRedisSerializer)) // Value Serializer 변경
			.disableCachingNullValues()
			.entryTtl(Duration.ofMinutes(10L));

		builder.cacheDefaults(configuration);

		return builder.build();
	}

}