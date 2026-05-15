package com.example.demo.service.auth;


import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.user.User;
import com.example.demo.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationMs;

    public RefreshToken getRefreshTokenData(UUID refreshToken) {
        Optional<RefreshToken> refreshTokenData =
                refreshTokenRepository.findByRefreshTokenId(refreshToken);

        if (!refreshTokenData.isPresent()) {
            throw new EntityNotFoundException("Refresh token not found: " + refreshToken);
        }
        return refreshTokenData.get();
    }

    public RefreshToken createRefreshToken(User user, String tokenHash) {
        refreshTokenRepository.deleteAllByUserUserId(user.getUserId());

        RefreshToken refreshToken = new RefreshToken(user, tokenHash, refreshExpirationMs);

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isValidRefreshToken(RefreshToken refreshTokenData) {
        if (refreshTokenData.isRevoked() || isExpired(refreshTokenData)) {
            return false;
        }

        return true;
    }

    public RefreshToken getRefreshTokenData(String refreshToken) {
        String currentTokenHash = getHashToken(refreshToken);
        Optional<RefreshToken> refreshTokenData =
                refreshTokenRepository.findByTokenHash(currentTokenHash);

        if (!refreshTokenData.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found");
        }

        return refreshTokenData.get();
    }

    public String getHashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    public boolean deleteRefreshTokenByUserId(UUID userId) {
        refreshTokenRepository.deleteAllByUserUserId(userId);

        return true;
    }

    private boolean isExpired(RefreshToken refreshTokenData) {
        return refreshTokenData.getExpiresAt().isBefore(LocalDateTime.now());
    }
}