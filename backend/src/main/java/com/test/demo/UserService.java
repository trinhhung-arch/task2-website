package com.test.demo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

	private static final Set<String> AVATAR_TYPES = Set.of("image/png", "image/jpeg", "image/gif", "image/webp");

	private final UserRepository userRepository;
	private final LoginHistoryRepository loginHistoryRepository;
	private final AvatarRepository avatarRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public UserService(UserRepository userRepository, LoginHistoryRepository loginHistoryRepository,
			AvatarRepository avatarRepository) {
		this.userRepository = userRepository;
		this.loginHistoryRepository = loginHistoryRepository;
		this.avatarRepository = avatarRepository;
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

	// Every attempt on an existing account is recorded in its login history, failed ones too
	public Optional<Users> authenticate(String email, String password) {
		Optional<Users> account = findByEmail(email);
		boolean success = account.isPresent() && password != null
				&& passwordEncoder.matches(password, account.get().getPassword());
		account.ifPresent(user -> loginHistoryRepository.save(new LoginHistory(user.getId(), success)));
		return success ? account : Optional.empty();
	}

	// The signed-in user for a session's user id, or 401 when nobody is logged in
	public Users requireUser(Long userId) {
		return Optional.ofNullable(userId)
				.flatMap(userRepository::findById)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in"));
	}

	public List<LoginHistory> loginHistory(Users user) {
		return loginHistoryRepository.findByUserIdOrderByLoginTimeDescIdDesc(user.getId());
	}

	public void changePassword(Users user, String currentPassword, String newPassword) {
		if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your current password is incorrect");
		}
		if (newPassword == null || newPassword.length() < 6) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be at least 6 characters");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public void saveAvatar(Users user, String contentType, byte[] data) {
		// Raster images only: an uploaded SVG could carry script
		if (data.length == 0 || contentType == null || !AVATAR_TYPES.contains(contentType)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please choose a PNG, JPEG, GIF or WebP image");
		}
		avatarRepository.save(new Avatar(user.getId(), contentType, data));
	}

	public Optional<Avatar> findAvatar(Users user) {
		return avatarRepository.findById(user.getId());
	}
}
