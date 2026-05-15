package com.example.demo.entity.user;

import com.example.demo.entity.RefreshToken;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false, unique = true)
    @Getter
    @Setter
    UUID userId;

    @Column(name = "login", length = 50, nullable = false, unique = true)
    @Getter
    @Setter
    String login;

    @Column(name = "phone", length = 15, nullable = false, unique = true)
    @Getter
    @Setter
    String phone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "role", length = 20, nullable = false)
    @Getter
    @Setter
    UserRole role = UserRole.USER;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    @Getter
    @Setter
    String email;

    @Column(name = "password_hash", length = 250, nullable = false)
    @Getter
    @Setter
    String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Getter
    @Setter
    Instant createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

    public User(String login, String phone, String email, String passwordHash) {
        this.login = login;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = UserRole.USER;
    }

    public User(String login, String phone, String email, String passwordHash, UserRole role) {
        this.login = login;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

}