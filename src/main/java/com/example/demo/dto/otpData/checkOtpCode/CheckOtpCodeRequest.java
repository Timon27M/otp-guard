package com.example.demo.dto.otpData.checkOtpCode;

import java.util.UUID;

public record CheckOtpCodeRequest(UUID operationId, String otpCode) {
}
