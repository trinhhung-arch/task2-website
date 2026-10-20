package com.test.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

	List<LoginHistory> findByUserIdOrderByLoginTimeDescIdDesc(Long userId);
}
