package com.dev.SecOne.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.SecOne.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	// Jpa 쿼리 메서드
	
	User findByUsername(String username);
}
