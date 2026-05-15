package com.example.demo.repository;

import com.example.demo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByRefreshTokenId(UUID refreshTokenId);

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  Optional<RefreshToken> findByUserUserId(UUID userId);

  void deleteAllByUserUserId(UUID userId);

  void deleteAllByUserUserIdAndRevokedFalse(UUID userId);
}
