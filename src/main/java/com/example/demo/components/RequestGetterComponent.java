package com.example.demo.components;

import com.example.demo.service.auth.JwtService;
import com.example.demo.utils.CookiesUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestGetterComponent {
  private final JwtService jwtService;

  public UUID getCurrentUserId() {
    Cookie[] cookies = getAllCookies();

    if (cookies == null) {
      throw new AuthenticationCredentialsNotFoundException("User not authenticated");
    }

    String accessToken = CookiesUtil.extractTokenFromCookies(cookies, "access_token");

    if (accessToken == null) {
      throw new AuthenticationCredentialsNotFoundException("User not authenticated");
    }

    return jwtService.extractUserInfo(accessToken).userId();
  }

  public Cookie[] getAllCookies() {
    HttpServletRequest request = getCurrentRequest();
    return request.getCookies();
  }

  private HttpServletRequest getCurrentRequest() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      throw new RuntimeException("No request bound to current thread");
    }

    return attributes.getRequest();
  }
}
