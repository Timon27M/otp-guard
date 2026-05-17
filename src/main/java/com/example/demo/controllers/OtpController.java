package com.example.demo.controllers;

import com.example.demo.dto.global.DefaultSuccessResponse;
import com.example.demo.dto.otpData.checkOtpCode.CheckOtpCodeRequest;
import com.example.demo.dto.otpData.generateOtp.GenerateOtpRequest;
import com.example.demo.dto.otpData.generateOtp.GenerateOtpResponse;
import com.example.demo.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/generate")
    public GenerateOtpResponse generateOtp(@RequestBody GenerateOtpRequest request) {
        UUID operationId = otpService.generateOtp(request);
        return new GenerateOtpResponse(operationId);
    }

    @PostMapping("/check")
    public DefaultSuccessResponse checkOtpCode(@RequestBody CheckOtpCodeRequest request) {
        otpService.checkOtpCode(request.operationId(), request.otpCode());
        return new DefaultSuccessResponse();
    }

}
