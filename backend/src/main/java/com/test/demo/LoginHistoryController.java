package com.test.demo;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/login-history")
public class LoginHistoryController {

	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final UserService userService;

	public LoginHistoryController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<Entry> list(@SessionAttribute(name = UserController.SESSION_USER_ID, required = false) Long userId) {
		Users user = userService.requireUser(userId);
		return userService.loginHistory(user).stream()
				.map(h -> new Entry(user.getName(), user.getEmail(), TIME.format(h.getLoginTime()), h.isSuccess()))
				.toList();
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> export(
			@SessionAttribute(name = UserController.SESSION_USER_ID, required = false) Long userId) {
		List<List<String>> rows = list(userId).stream()
				.map(e -> List.of(e.name(), e.email(), e.loginTime(), e.success() ? "Success" : "Failed"))
				.toList();
		return Excel.download("login-history.xlsx", "Login history",
				List.of("Name", "Email", "Login time", "Result"), rows);
	}

	public record Entry(String name, String email, String loginTime, boolean success) {
	}
}
