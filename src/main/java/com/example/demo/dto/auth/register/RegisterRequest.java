package com.example.demo.dto.auth.register;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterRequest {
    @Getter
    private final String login;
    @Getter
    private final String password;
    @Getter
    private final String email;
    @Getter
    private final String phone;
}
