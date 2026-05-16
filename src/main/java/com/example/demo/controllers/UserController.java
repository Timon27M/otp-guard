package com.example.demo.controllers;

import com.example.demo.dto.global.DefaultSuccessResponse;
import com.example.demo.dto.user.getAllUsers.GetAllUsersResponse;
import com.example.demo.entity.user.User;
import com.example.demo.entity.user.UserRole;
import com.example.demo.mappers.UserMapper;
import com.example.demo.service.auth.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PatchMapping("/update-role")
    public DefaultSuccessResponse updateUserRole(@RequestParam String role, HttpServletResponse response) {
        log.info("update user role: {}", role);
        String res = userService.updateRoleUser(UserRole.valueOf(role), response);

        return new DefaultSuccessResponse(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public GetAllUsersResponse getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<GetAllUsersResponse.CustomUser> customUsers = users.stream()
                .map(UserMapper::mapToCustomUser)
                .toList();

        return new GetAllUsersResponse(customUsers);
    }
}
