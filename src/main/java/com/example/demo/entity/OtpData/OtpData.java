package com.example.demo.entity.OtpData;

import com.example.demo.entity.Operation;
import com.example.demo.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "otp_data")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "otp_id", updatable = false, nullable = false, unique = true)
    @Getter
    @Setter
    UUID otpId;

    @Column(name = "operation_id", nullable = false)
    @Getter
    @Setter
    UUID operationId;

    @Column(name = "code_hash", length = 255, nullable = false)
    @Getter
    @Setter
    String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Getter
    @Setter
    OtpStatus status = OtpStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Getter
    @Setter
    Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    @Getter
    @Setter
    Instant expiresAt;

    @Column(name = "used_at")
    @Getter
    @Setter
    Instant usedAt;

    @Column(name = "user_id", nullable = false)
    @Getter
    @Setter
    UUID userId;

    public OtpData(UUID operationId, String codeHash, UUID userId, Integer expiresTime) {
        this.operationId = operationId;
        this.codeHash = codeHash;
        this.userId = userId;
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plus(expiresTime, ChronoUnit.MINUTES);
    }
}