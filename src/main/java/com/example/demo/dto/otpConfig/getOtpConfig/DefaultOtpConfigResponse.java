package com.example.demo.dto.otpConfig.getOtpConfig;

import java.time.Instant;
import java.util.UUID;

public record DefaultOtpConfigResponse(String status, Integer configId, Integer codeLength, Integer lifetimeMinutes, Instant updatedAt,
                                       UUID updatedBy) {
}
