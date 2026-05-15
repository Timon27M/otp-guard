package com.example.demo.entity;

import com.example.demo.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
  @Id
  @UuidGenerator(style = UuidGenerator.Style.RANDOM)
  @JdbcTypeCode(SqlTypes.UUID)
  @Column(
      name = "refresh_token_id",
      updatable = false,
      nullable = false,
      columnDefinition = "UUID",
      unique = true)
  private UUID refreshTokenId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      unique = true,
      updatable = false,
      foreignKey = @ForeignKey(name = "fk_refresh_token_user"))
  private User user;

  @Column(name = "token_hash", nullable = false, length = 64)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "revoked", nullable = false)
  private boolean revoked;

  public RefreshToken(User user, String tokenHash, long refreshExpirationMs) {
    this.user = user;
    this.createdAt = LocalDateTime.now();
    this.tokenHash = tokenHash;
    this.expiresAt = LocalDateTime.now().plus(refreshExpirationMs, ChronoUnit.MILLIS);
  }

  public RefreshToken() {}

  public void setRevoked() {
    this.revoked = true;
  }

  public UUID getRefreshTokenId() {
    return refreshTokenId;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public boolean isRevoked() {
    return revoked;
  }

  public User getUser() {
    return user;
  }
}
