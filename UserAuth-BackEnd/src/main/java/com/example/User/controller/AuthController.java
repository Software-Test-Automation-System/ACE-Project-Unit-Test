package com.example.User.controller;

import com.example.User.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtutil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtutil) {
        this.authenticationManager = authenticationManager;
        this.jwtutil = jwtutil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password) {

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("email or password is missing");
        }

        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            System.out.println("AuthToken created: " + authToken);
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Authentication successful for user: " + email);
            String accessToken = jwtutil.generateAccessToken(email);
            String refreshToken = jwtutil.generateRefreshToken(email);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            e.getStackTrace();
            return ResponseEntity.status(500).body("Error : " + e);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            String email = jwtutil.extractEmail(refreshToken, true);
            if (jwtutil.validateToken(refreshToken, true)) {
                String newAccessToken = jwtutil.generateAccessToken(email);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", newAccessToken);
                return ResponseEntity.ok(tokens);
            } else {
                return ResponseEntity.status(401).body("Invalid refresh token or expired");

            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }
}