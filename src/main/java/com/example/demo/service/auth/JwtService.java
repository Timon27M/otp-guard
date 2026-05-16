package com.example.demo.service.auth;

import com.example.demo.config.JwtKeyConfig;
import com.example.demo.dto.records.TokenUserInfo;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.user.User;
import com.example.demo.entity.user.UserRole;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.CookiesUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtKeyConfig jwtKeyConfig;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;


    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("login", user.getLogin())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(jwtKeyConfig.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("type", "refresh")
                .claim("tokenId", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(jwtKeyConfig.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public TokenUserInfo extractUserInfo(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtKeyConfig.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        UUID userId = UUID.fromString(claims.getSubject());
        String login = claims.get("login", String.class);
        String roleStr = claims.get("role", String.class);

        UserRole role = UserRole.valueOf(roleStr);

        return new TokenUserInfo(userId, login, role);

    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims =
                    Jwts.parser()
                            .verifyWith(jwtKeyConfig.getPublicKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            UUID userId = UUID.fromString(claims.getSubject());

            if (!userRepository.existsById(userId)) {
                return false;
            }

            return true;
        } catch (MalformedJwtException | ExpiredJwtException | IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String updateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        String refreshToken = CookiesUtil.extractTokenFromCookies(cookies, "refresh_token");

        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not transferred");
        }

        RefreshToken refreshTokenData = refreshTokenService.getRefreshTokenData(refreshToken);

        if (!refreshTokenService.isValidRefreshToken(refreshTokenData)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        User user = refreshTokenData.getUser();
        String token = generateAccessToken(user);

        Cookie accessCookie = new Cookie("access_token", token);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(15 * 60);

        String newRefreshToken = generateRefreshToken(user);
        String newRefreshTokenHash = refreshTokenService.getHashToken(newRefreshToken);

        refreshTokenService.createRefreshToken(user, newRefreshTokenHash);

        Cookie refreshCookie = new Cookie("refresh_token", newRefreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return token;
    }
}
