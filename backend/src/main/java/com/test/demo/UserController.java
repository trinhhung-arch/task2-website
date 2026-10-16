package com.test.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserProfile> register(@RequestBody RegisterUserRequest request) {
		Users user = userService.register(
				request.name(),
				request.email(),
				request.password());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new UserProfile(user.getId(), user.getName(), user.getEmail()));
	}

	public record RegisterUserRequest(String name, String email, String password) {
	}

	public record UserProfile(Long id, String name, String email) {
	}
}
