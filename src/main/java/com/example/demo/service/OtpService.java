package com.example.demo.service;

import com.example.demo.dto.otpData.generateOtp.GenerateOtpRequest;
import com.example.demo.entity.Operation;
import com.example.demo.entity.OtpConfig;
import com.example.demo.entity.OtpData.OtpData;
import com.example.demo.entity.OtpData.OtpStatus;
import com.example.demo.entity.user.User;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.notification.INotificationService;
import com.example.demo.service.notification.NotificationFactory;
import com.example.demo.utils.ChannelEnum;
import com.example.demo.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    private final OperationsService operationsService;
    private final UserRepository userRepository;
    private final OtpConfigService otpConfigService;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final NotificationFactory notificationFactory;

    @Transactional
    public UUID generateOtp(GenerateOtpRequest request) {

        UUID currentUserId = (UUID) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        log.info("Generating OTP for user {}", currentUserId);

        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Operation operation = operationsService.createOperation(request.operationName(), user);
        OtpConfig config = otpConfigService.getConfig();

        String code = CodeGenerator.generateCode(config.getCodeLength());
        log.info("code: {}", code);

        String hashCode = passwordEncoder.encode(code);

        otpRepository.save(new OtpData(operation.getOperationId(), hashCode, user.getUserId(), config.getLifetimeMinutes()));

        String recipient = switch (request.channel()) {
            case EMAIL -> user.getEmail();
            case SMS -> user.getPhone();
            case TELEGRAM -> user.getLogin();
        };

        INotificationService notificationService = notificationFactory.getService(request.channel().name());

        notificationService.sendCode(recipient, code);

        return operation.getOperationId();
    }

    @Transactional
    public boolean checkOtpCode(UUID operationId, String otpCode) {
        OtpData otpData = otpRepository.findByOperationId(operationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OTP not found"));

        UUID currentUserId = (UUID) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (!otpData.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OTP does not match");
        }

        if (otpData.getStatus() == OtpStatus.EXPIRED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP code expired");
        }

        if (otpData.getStatus() == OtpStatus.USED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP code already used");
        }

        if (otpData.getExpiresAt().isBefore(Instant.now())) {
            otpData.setStatus(OtpStatus.EXPIRED);
            otpRepository.save(otpData);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP code expired");
        }

        if (!passwordEncoder.matches(otpCode, otpData.getCodeHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP code");
        }

        otpData.setStatus(OtpStatus.USED);
        otpData.setUsedAt(Instant.now());
        otpRepository.save(otpData);

        return true;
    }

    @Scheduled(fixedDelay = 120000)
    @Transactional
    public void expireOldOtps() {
        int expiredCount = otpRepository.expireOldCodes();
        log.info("Expired {} OTP codes", expiredCount);
    }
}
