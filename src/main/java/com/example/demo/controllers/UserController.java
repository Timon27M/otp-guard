package com.example.demo.controllers;

import com.example.demo.dto.global.DefaultSuccessResponse;
import com.example.demo.dto.user.getAllUsers.GetAllUsersResponse;
import com.example.demo.entity.user.User;
import com.example.demo.entity.user.UserRole;
import com.example.demo.mappers.UserMapper;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PatchMapping("/update-role")
    public DefaultSuccessResponse updateUserRole(@RequestParam String role, HttpServletResponse response) {
        log.info("Запрос на обновление пользователя выполняется...");
        String res = userService.updateRoleUser(UserRole.valueOf(role), response);

        log.info("Запрос выполнился успешно!");
        return new DefaultSuccessResponse(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public GetAllUsersResponse getAllUsers() {
        log.info("Запрос на получение всех пользователей выполняется...");
        List<User> users = userService.getAllUsers();
        List<GetAllUsersResponse.CustomUser> customUsers = users.stream()
                .map(UserMapper::mapToCustomUser)
                .toList();
        log.info("Запрос выполнился успешно!");

        return new GetAllUsersResponse(customUsers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public DefaultSuccessResponse deleteUser(@RequestParam String login) {
        log.info("Запрос на удаление пользователя выполняется...");
        userService.deleteUser(login);
        log.info("Запрос выполнился успешно!");

        return new DefaultSuccessResponse("Deleted user: " + login);
    }
}
