package com.example.demo.utils;

import jakarta.servlet.http.Cookie;

public class CookiesUtil {
  public static String extractTokenFromCookies(Cookie[] cookies, String name) {
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
