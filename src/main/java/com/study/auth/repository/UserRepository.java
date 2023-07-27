package com.study.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.auth.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByEmail(String email);
	
}
