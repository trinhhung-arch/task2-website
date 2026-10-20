package com.test.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	List<Todo> findByUserIdOrderByIdDesc(Long userId);

	Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
