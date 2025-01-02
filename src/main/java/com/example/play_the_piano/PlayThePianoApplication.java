package com.example.play_the_piano;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@MapperScan("com.example.play_the_piano")
public class PlayThePianoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayThePianoApplication.class, args);
	}

}
