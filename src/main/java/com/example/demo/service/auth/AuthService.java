package com.example.demo.service.auth;

import com.example.demo.components.RequestGetterComponent;
import com.example.demo.dto.auth.login.LoginRequest;
import com.example.demo.entity.RefreshToken;
import com.example.demo.utils.CookiesUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.example.demo.dto.auth.register.RegisterRequest;
import com.example.demo.entity.user.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RequestGetterComponent requestGetterComponent;

    public User register(RegisterRequest request) {
        if (!checkUniqueData(request.getLogin(), request.getEmail(), request.getPhone())) {
            throw new IllegalArgumentException("Login or Email or Phone already exists");
        }

        User user = new User(request.getLogin(), request.getPhone(), request.getEmail(), passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    @Transactional
    public String login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        User user =
                userRepository
                        .findByLogin(loginRequest.getLogin())
                        .orElseThrow(() -> new IllegalArgumentException("Неверный логин"));

        Cookie[] cookies = request.getCookies();
        String currentAccessToken = CookiesUtil.extractTokenFromCookies(cookies, "access_token");
        String currentRefreshToken = CookiesUtil.extractTokenFromCookies(cookies, "refresh_token");

        if (currentRefreshToken != null || currentAccessToken != null) {
            throw new IllegalArgumentException("Необходимо выйти из аккаунта (logout)");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        String token = jwtService.generateAccessToken(user);

        Cookie accessCookie = new Cookie("access_token", token);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(1 * 10);

        String refreshToken = jwtService.generateRefreshToken(user);

        String refreshTokenHash = refreshTokenService.getHashToken(refreshToken);

        refreshTokenService.createRefreshToken(user, refreshTokenHash);

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return "Success authorization";
    }

    @Transactional
    public String logoutUser(HttpServletResponse response) {

        UUID userId;

        try {
            userId = requestGetterComponent.getCurrentUserId();
            refreshTokenService.deleteRefreshTokenByUserId(userId);
        } catch (AuthenticationCredentialsNotFoundException e) {
            System.out.println(e.getMessage());
        }

        Cookie accessCookie = new Cookie("access_token", "");
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refresh_token", "");
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return "Success logout";
    }

    boolean checkUniqueData(String login, String email, String phone) {
        if (userRepository.existsByLogin(login) || userRepository.existsByPhone(phone) || userRepository.existsByEmail(email)) {
            return false;
        }

        return true;
    }
}
