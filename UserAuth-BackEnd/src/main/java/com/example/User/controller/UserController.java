package com.example.User.controller;

import com.example.User.config.JwtUtil;
import com.example.User.dto.PageResponse;
import com.example.User.entities.User;
import com.example.User.exception.EmailAlreadyUsedException;
import com.example.User.repositories.UserRepository;
import com.example.User.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<PageResponse<User>> getAllUser(@RequestParam("size") int size,
                                                         @RequestParam("pageNum") int pageNum,
                                                         @RequestParam(value = "search", defaultValue = "") String search) {

        return ResponseEntity.ok(userService.findAllUsers(size, pageNum, search));
    }

    @GetMapping("all")
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        @RequestParam("password") String password) {

        try {
            // Handle image upload if needed, or ignore if not required.
            User user = userService.createUser(username, email, password);
            return ResponseEntity.status(201).body(Map.of(
                    "message", "User created successfully",
                    "userId", user.getId()
            ));
        } catch (EmailAlreadyUsedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@RequestParam(value = "id") Long id,
                                        @RequestParam(value = "username", required = false) String username,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "password", required = false) String password) {

        try {
            // Handle image upload logic if necessary, or omit completely.
            String imageUrl = null; // Placeholder if needed
            User user = userService.updateUser(id, username, email, password, imageUrl);
            return ResponseEntity.status(200).body(Map.of(
                    "message", "User updated successfully",
                    "userId", user.getId()
            ));
        } catch (com.example.User.exception.UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (EmailAlreadyUsedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@RequestParam("id") Long id) {
        try {
            Optional<User> user = userService.findUserById(id);
            userService.deleteUserById(id);
            return ResponseEntity.status(200).body(Map.of(
                    "message", "User deleted successfully",
                    "userId", id
            ));
        } catch (com.example.User.exception.UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
