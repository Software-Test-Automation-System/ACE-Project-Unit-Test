package com.example.User.repositories;

import com.example.User.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String Email);

    Page<User> findAllByUsernameContainingIgnoreCase(String username, Pageable pageable);

    boolean existsByEmail(String email);
}