package com.test.demo;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

// Every API error comes back as {"message": "..."} so the pages can show it as-is
@RestControllerAdvice
public class ApiErrorHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, String>> onError(ResponseStatusException e) {
		return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Map<String, String>> onTooLarge(MaxUploadSizeExceededException e) {
		return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(Map.of("message", "Image must be 2 MB or smaller"));
	}
}
