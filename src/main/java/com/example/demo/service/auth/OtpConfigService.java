package com.example.demo.service.auth;


import com.example.demo.entity.OtpConfig;
import com.example.demo.entity.user.User;
import com.example.demo.repository.OtpConfigRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpConfigService {
    private final OtpConfigRepository otpConfigRepository;
    private final UserRepository userRepository;

    public OtpConfig getConfig() {
        return otpConfigRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("OTP configuration not found"));
    }

    @Transactional
    public OtpConfig updateCodeLength(Integer codeLength) {
        if (codeLength > 10 || codeLength < 4) {
            throw new RuntimeException("Invalid code length");
        }

        UUID currentUserId = (UUID) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findById(currentUserId);
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }

        OtpConfig config = getConfig();

        config.setCodeLength(codeLength);
        config.setUpdatedBy(user.get());
        config.setUpdatedAt(new Date().toInstant());

        return otpConfigRepository.save(config);
    }

    @Transactional
    public OtpConfig updateLifetimeMinutes(Integer lifetimeMinutes) {
        if (lifetimeMinutes > 10 || lifetimeMinutes < 2) {
            throw new RuntimeException("Invalid life time minutes");
        }

        UUID currentUserId = (UUID) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findById(currentUserId);
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }

        OtpConfig config = getConfig();

        config.setLifetimeMinutes(lifetimeMinutes);
        config.setUpdatedBy(user.get());
        config.setUpdatedAt(new Date().toInstant());

        return otpConfigRepository.save(config);
    }

}