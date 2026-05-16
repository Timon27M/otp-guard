package com.example.demo.dto.user.getAllUsers;

import com.example.demo.entity.user.UserRole;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
public class GetAllUsersResponse {
    private final List<CustomUser> users;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomUser {
        private UUID userId;
        private String login;
        private String email;
        private String phone;
        private UserRole role;
    }
}
