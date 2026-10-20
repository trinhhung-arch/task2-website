package com.test.demo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login_history")
public class LoginHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private LocalDateTime loginTime;

	@Column(nullable = false)
	private boolean success;

	protected LoginHistory() {
	}

	public LoginHistory(Long userId, boolean success) {
		this.userId = userId;
		this.success = success;
		this.loginTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public LocalDateTime getLoginTime() {
		return loginTime;
	}

	public boolean isSuccess() {
		return success;
	}
}
