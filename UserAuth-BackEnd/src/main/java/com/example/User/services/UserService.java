package com.example.User.services;

import com.example.User.dto.PageResponse;
import com.example.User.entities.User;
import com.example.User.exception.EmailAlreadyUsedException;

import java.util.Optional;

public interface UserService {
    User createUser(String username, String email, String password) throws EmailAlreadyUsedException;
    User updateUser(Long id, String username, String email, String password, String profileImageUrl);
    Optional<User> findUserById(Long id);
    void deleteUserById(Long id);

    PageResponse<User> findAllUsers(int size, int pageNum, String search);
}
