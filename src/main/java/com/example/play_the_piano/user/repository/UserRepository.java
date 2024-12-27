package com.example.play_the_piano.user.repository;

import com.example.play_the_piano.user.entity.User;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {

	void insertUser(User user);

	Optional<Boolean> existsByUsername(String username);

	Optional<Boolean> existsByNickname(String nickname);

	Optional<User> findByUsername(String username);

	Optional<String> findUsernameByEmail(String email);

	Optional<Long> findIdByEmail(String email);

	void updatePassword(Long id,String password);

}
