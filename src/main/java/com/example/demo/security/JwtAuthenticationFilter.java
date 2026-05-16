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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/auth/");
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String requestId = UUID.randomUUID().toString();
//        MDC.put("requestId", requestId);
//
//        log.info("🔐 Начало аутентификации: {} {} | IP: {} | RequestId: {}",
//                request.getMethod(),
//                request.getRequestURI(),
//                request.getRemoteAddr(),
//                requestId);
//
//        Cookie[] cookies = request.getCookies();
//        String accessToken = CookiesUtil.extractTokenFromCookies(cookies, "access_token");
//
//        if (accessToken == null) {
//            try {
//                String newAccessToken = jwtService.updateAccessToken(request, response);
//                authenticateUser(newAccessToken, request);
//            } catch (Exception e) {
//                request.setAttribute(
//                        "authException", new ServletException("Ошибка аутентификации: токена не существует"));
//                throw new ServletException(
//                        "Невозможно установить аутентификацию пользователя (токена не существует): ");
//            }
//        } else {
//            if (jwtService.isTokenValid(accessToken)) {
//                authenticateUser(accessToken, request);
//            } else {
//                try {
//                    String newAccessToken = jwtService.updateAccessToken(request, response);
//                    authenticateUser(newAccessToken, request);
//                } catch (Exception e) {
//                    request.setAttribute(
//                            "authException",
//                            new ServletException("Ошибка аутентификации токен просрочен или отозван: "));
//                    throw new ServletException(
//                            "Невозможно установить аутентификацию пользователя (токен просрочен или отозван): ");
//                }
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Генерируем requestId в самом начале
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        // Логируем начало обработки запроса
        log.info("🔐 Начало аутентификации: {} {} | IP: {} | RequestId: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                requestId);

        long startTime = System.currentTimeMillis();

        try {
            Cookie[] cookies = request.getCookies();
            String accessToken = CookiesUtil.extractTokenFromCookies(cookies, "access_token");

            // Логируем наличие токена
            if (accessToken == null) {
                log.warn("❌ Access token отсутствует в cookies. Пытаемся обновить...");
            } else {
                log.debug("✅ Access token найден: {}", maskToken(accessToken));
            }

            if (accessToken == null) {
                try {
                    log.info("🔄 Попытка обновления access token (токен отсутствует)");
                    String newAccessToken = jwtService.updateAccessToken(request, response);
                    authenticateUser(newAccessToken, request);
                    log.info("✅ Аутентификация успешно выполнена после обновления токена");
                } catch (Exception e) {
                    log.error("❌ Ошибка при обновлении токена: {}", e.getMessage(), e);
                    request.setAttribute(
                            "authException",
                            new ServletException("Ошибка аутентификации: токена не существует"));
                    throw new ServletException(
                            "Невозможно установить аутентификацию пользователя (токена не существует): ", e);
                }
            } else {
                if (jwtService.isTokenValid(accessToken)) {
                    log.debug("✅ Токен валиден, аутентифицируем пользователя");
                    authenticateUser(accessToken, request);
                    log.info("✅ Аутентификация успешна для пользователя");
                } else {
                    log.warn("⚠️ Токен не валиден (просрочен или отозван). Пытаемся обновить...");
                    try {
                        String newAccessToken = jwtService.updateAccessToken(request, response);
                        authenticateUser(newAccessToken, request);
                        log.info("✅ Аутентификация успешна после обновления просроченного токена");
                    } catch (Exception e) {
                        log.error("❌ Ошибка при обновлении просроченного токена: {}", e.getMessage(), e);
                        request.setAttribute(
                                "authException",
                                new ServletException("Ошибка аутентификации токен просрочен или отозван: "));
                        throw new ServletException(
                                "Невозможно установить аутентификацию пользователя (токен просрочен или отозван): ", e);
                    }
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ Аутентификация завершена за {} ms | RequestId: {}", duration, requestId);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("❌ Критическая ошибка в процессе аутентификации: {}", e.getMessage(), e);
            throw e;
        } finally {
            // Очищаем MDC после завершения запроса
            MDC.clear();
        }
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

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }

}
