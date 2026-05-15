package com.example.demo.security;

import com.example.demo.dto.records.TokenUserInfo;
import com.example.demo.entity.user.UserRole;
import com.example.demo.service.auth.JwtService;
import com.example.demo.utils.CookiesUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        System.out.println("PATH: " + path);

        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        String accessToken = CookiesUtil.extractTokenFromCookies(cookies, "access_token");

        if (accessToken == null) {
            try {
                String newAccessToken = jwtService.updateAccessToken(request, response);
                authenticateUser(newAccessToken, request);
            } catch (Exception e) {
                request.setAttribute(
                        "authException", new ServletException("Ошибка аутентификации: токена не существует"));
                throw new ServletException(
                        "Невозможно установить аутентификацию пользователя (токена не существует): ");
            }
        } else {
            if (jwtService.isTokenValid(accessToken)) {
                authenticateUser(accessToken, request);
            } else {
                try {
                    String newAccessToken = jwtService.updateAccessToken(request, response);
                    authenticateUser(newAccessToken, request);
                } catch (Exception e) {
                    request.setAttribute(
                            "authException",
                            new ServletException("Ошибка аутентификации токен просрочен или отозван: "));
                    throw new ServletException(
                            "Невозможно установить аутентификацию пользователя (токен просрочен или отозван): ");
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public void authenticateUser(String accessToken, HttpServletRequest request) throws ServletException {
        try {
            TokenUserInfo userInfo = jwtService.extractUserInfo(accessToken);
            UUID userId = userInfo.userId();
            UserRole role = userInfo.role();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            request.setAttribute("authException", new ServletException("Ошибка аутентификации: "));
            throw new ServletException("Невозможно установить аутентификацию пользователя: ", e);
        }
    }

}
