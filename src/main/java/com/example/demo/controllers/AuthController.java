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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        log.info("Запрос на регистрацию выполняется...");
        User user = authService.register(request);

        log.info("Запрос выполнился успешно!");
        return user;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        log.info("Запрос на авторизацию выполняется...");
        String result = authService.login(loginRequest, request, response);
        log.info("Запрос выполнился успешно!");

        return result;
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            log.info("Запрос на выход выполняется...");
            String responseText = authService.logoutUser(response);
            log.info("Запрос выполнился успешно!");
            return ResponseEntity.ok().body(new DefaultSuccessResponse(responseText));
        } catch (Exception e) {
            log.error("Запрос завершился ошибкой!");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ERROR", "message", e.getMessage(), "статус", 403));
        }
    }
}

