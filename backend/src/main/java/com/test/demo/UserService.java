package com.test.demo;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Users register(String name, String email, String password) {
		Users user = new Users(
				name.trim(),
				email.trim().toLowerCase(),
				passwordEncoder.encode(password));
		return userRepository.save(user);
	}

	public Optional<Users> findByEmail(String email) {
		if (email == null || email.isBlank()) {
			return Optional.empty();
		}
		return userRepository.findByEmail(email.trim().toLowerCase());
	}

	public boolean authenticate(String email, String password) {
		if (password == null) {
			return false;
		}
		return findByEmail(email)
				.map(user -> passwordEncoder.matches(password, user.getPassword()))
				.orElse(false);
	}
}
