package com.example.demo.dto.records;

import com.example.demo.entity.user.UserRole;

import java.util.UUID;

public record TokenUserInfo(UUID userId, String login, UserRole role) {}
