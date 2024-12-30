package com.example.User.services;

import com.example.User.dto.PageResponse;
import com.example.User.entities.User;
import com.example.User.exception.EmailAlreadyUsedException;
import com.example.User.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email already in use");
        }
        User user = User.builder()
                .username(username.toLowerCase())
                .email(email.toLowerCase())
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(user);
    }


    @Override
    public User updateUser(Long id, String username, String email, String password, String profileImageUrl) {
        User user = userRepository.findById(id).orElseThrow(() -> new com.example.User.exception.UserNotFoundException("user ID : " + id + " not found"));
        if (username != null && !username.isEmpty()) {
            user.setUsername(username);
        }
        if (email != null && !email.isEmpty() && !userRepository.existsByEmail(email)) {
            user.setEmail(email);
        } else if (email != null) {
            throw new EmailAlreadyUsedException("Email already in use");
        }
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }


        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id).isPresent() ? userRepository.findById(id) : Optional.empty();
    }


    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public PageResponse<User> findAllUsers(int size, int pageNum, String search) {
        return null;
    }

}
