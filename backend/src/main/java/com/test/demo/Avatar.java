package com.test.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

// Kept apart from Users so loading a user never drags the image bytes along
@Entity
@Table(name = "avatars")
public class Avatar {

	@Id
	private Long userId;

	@Column(nullable = false, length = 100)
	private String contentType;

	@Lob
	@Column(nullable = false)
	private byte[] data;

	protected Avatar() {
	}

	public Avatar(Long userId, String contentType, byte[] data) {
		this.userId = userId;
		this.contentType = contentType;
		this.data = data;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getData() {
		return data;
	}
}
