package com.test.demo;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Users register(String name, String email, String password) {
		if (name == null || name.isBlank() || email == null || !email.contains("@")
				|| password == null || password.length() < 6) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Please enter your name, a valid email and a password of at least 6 characters");
		}
		Users user = new Users(
				name.trim(),
				email.trim().toLowerCase(),
				passwordEncoder.encode(password));
		try {
			return userRepository.save(user);
		}
		catch (DataIntegrityViolationException e) {
			// The unique index on email is the source of truth, so concurrent sign-ups can't both win
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
		}
	}

	public Optional<Users> findByEmail(String email) {
		if (email == null || email.isBlank()) {
			return Optional.empty();
		}
		return userRepository.findByEmail(email.trim().toLowerCase());
	}

	public Optional<Users> findById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<Users> authenticate(String email, String password) {
		if (password == null) {
			return Optional.empty();
		}
		return findByEmail(email)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()));
	}
}
