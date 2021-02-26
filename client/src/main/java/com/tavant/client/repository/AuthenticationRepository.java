package com.tavant.client.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tavant.client.model.Authentication;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long>{
	 Optional<Authentication> findByUsername(String username);
	 
	 Boolean existsByUsername(String username);
	 
	 Boolean existsByEmail(String email);
}
