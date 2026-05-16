package com.example.demo.controllers;

import com.example.demo.dto.otpConfig.getOtpConfig.DefaultOtpConfigResponse;
import com.example.demo.entity.OtpConfig;
import com.example.demo.service.auth.OtpConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/otp-config")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OtpConfigController {
    private final OtpConfigService otpConfigService;


    @GetMapping
    public DefaultOtpConfigResponse getOtpConfig() {
        OtpConfig config = otpConfigService.getConfig();

        return new DefaultOtpConfigResponse(
                "SUCCESS",
                config.getConfigId(),
                config.getCodeLength(),
                config.getLifetimeMinutes(),
                config.getUpdatedAt(),
                config.getUpdatedBy() != null ? config.getUpdatedBy().getUserId() : null
        );
    }

    @Modifying
    @PatchMapping("/update/code-length")
    public DefaultOtpConfigResponse updateCodeLength(@RequestParam Integer codeLength) {
        OtpConfig config = otpConfigService.updateCodeLength(codeLength);

        return new DefaultOtpConfigResponse(
                "SUCCESS",
                config.getConfigId(),
                config.getCodeLength(),
                config.getLifetimeMinutes(),
                config.getUpdatedAt(),
                config.getUpdatedBy() != null ? config.getUpdatedBy().getUserId() : null
        );
    }

    @Modifying
    @PatchMapping("/update/lifetime-minutes")
    public DefaultOtpConfigResponse updateLifetimeMinutes(@RequestParam Integer lifetimeMinutes) {
        OtpConfig config = otpConfigService.updateLifetimeMinutes(lifetimeMinutes);

        return new DefaultOtpConfigResponse(
                "SUCCESS",
                config.getConfigId(),
                config.getCodeLength(),
                config.getLifetimeMinutes(),
                config.getUpdatedAt(),
                config.getUpdatedBy() != null ? config.getUpdatedBy().getUserId() : null
        );
    }
}
