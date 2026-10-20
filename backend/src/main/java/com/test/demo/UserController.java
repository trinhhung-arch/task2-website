package com.test.demo;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class UserController {

	static final String SESSION_USER_ID = "userId";

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/users")
	public ResponseEntity<UserProfile> register(@RequestBody RegisterUserRequest request) {
		Users user = userService.register(
				request.name(),
				request.email(),
				request.password());
		return ResponseEntity.status(HttpStatus.CREATED).body(UserProfile.of(user));
	}

	@PostMapping("/login")
	public UserProfile login(@RequestBody LoginRequest request, HttpServletRequest http) {
		Users user = userService.authenticate(request.email(), request.password())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
		http.getSession(true);
		http.changeSessionId(); // new session id on login to prevent session fixation
		http.getSession().setAttribute(SESSION_USER_ID, user.getId());
		return UserProfile.of(user);
	}

	@GetMapping("/me")
	public UserProfile me(@SessionAttribute(name = SESSION_USER_ID, required = false) Long userId) {
		return UserProfile.of(userService.requireUser(userId));
	}

	@PostMapping("/me/password")
	public ResponseEntity<Void> changePassword(@SessionAttribute(name = SESSION_USER_ID, required = false) Long userId,
			@RequestBody ChangePasswordRequest request) {
		userService.changePassword(userService.requireUser(userId), request.currentPassword(), request.newPassword());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/me/avatar")
	public ResponseEntity<Void> uploadAvatar(@SessionAttribute(name = SESSION_USER_ID, required = false) Long userId,
			@RequestParam("file") MultipartFile file) throws IOException {
		userService.saveAvatar(userService.requireUser(userId), file.getContentType(), file.getBytes());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me/avatar")
	public ResponseEntity<byte[]> avatar(@SessionAttribute(name = SESSION_USER_ID, required = false) Long userId) {
		Avatar avatar = userService.findAvatar(userService.requireUser(userId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No profile photo yet"));
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(avatar.getContentType()))
				.cacheControl(CacheControl.noCache())
				.header("X-Content-Type-Options", "nosniff")
				.body(avatar.getData());
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest http) {
		HttpSession session = http.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return ResponseEntity.noContent().build();
	}

	public record RegisterUserRequest(String name, String email, String password) {
	}

	public record LoginRequest(String email, String password) {
	}

	public record ChangePasswordRequest(String currentPassword, String newPassword) {
	}

	public record UserProfile(Long id, String name, String email) {

		static UserProfile of(Users user) {
			return new UserProfile(user.getId(), user.getName(), user.getEmail());
		}
	}
}
