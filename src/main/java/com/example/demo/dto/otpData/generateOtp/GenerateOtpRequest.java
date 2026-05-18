package com.example.demo.dto.otpData.generateOtp;

import com.example.demo.utils.ChannelEnum;

public record GenerateOtpRequest(String operationName, ChannelEnum channel) {
}
