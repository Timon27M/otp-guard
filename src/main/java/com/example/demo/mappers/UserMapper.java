package com.example.demo.mappers;

import com.example.demo.dto.user.getAllUsers.GetAllUsersResponse;
import com.example.demo.entity.user.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public static  GetAllUsersResponse.CustomUser mapToCustomUser(User user) {
        return new GetAllUsersResponse.CustomUser(
                user.getUserId(),
                user.getLogin(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}
