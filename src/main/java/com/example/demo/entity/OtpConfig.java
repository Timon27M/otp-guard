package com.example.demo.entity;

import com.example.demo.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "otp_config")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpConfig {

    @Id
    @Column(name = "config_id", nullable = false)
    @Getter
    @Setter
    Integer configId = 1;

    @Column(name = "code_length", nullable = false)
    @Getter
    @Setter
    Integer codeLength;

    @Column(name = "lifetime_minutes", nullable = false)
    @Getter
    @Setter
    Integer lifetimeMinutes;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Getter
    @Setter
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "user_id")
    @Getter
    @Setter
    User updatedBy;
}
