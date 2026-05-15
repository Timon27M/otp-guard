package com.example.demo.controllers;

import com.example.demo.dto.auth.login.LoginRequest;
import com.example.demo.dto.auth.register.RegisterRequest;
import com.example.demo.dto.global.DefaultSuccessResponse;
import com.example.demo.entity.user.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        return authService.login(loginRequest, request, response);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            String responseText = authService.logoutUser(response);
            return ResponseEntity.ok().body(new DefaultSuccessResponse(responseText));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ERROR", "message", e.getMessage(), "статус", 403));
        }
    }
}

