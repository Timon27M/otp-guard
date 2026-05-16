package com.example.demo.service.auth;

import com.example.demo.entity.user.User;
import com.example.demo.entity.user.UserRole;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public String updateRoleUser(UserRole role, HttpServletResponse response) {
        UUID currentUserId = (UUID) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        log.info("Current user id is {}", currentUserId);

        int user = userRepository.updateUserRole(currentUserId, role);
        if (user == 0) {
            throw new UsernameNotFoundException("User not found");
        }

        authService.logoutUser(response);

        log.info("User role updated, user needs to re-login");

        return "SUCCESS: User role updated, user needs to re-login";
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users;
    }
}
