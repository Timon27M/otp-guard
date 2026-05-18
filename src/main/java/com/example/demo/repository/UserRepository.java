package com.example.demo.repository;

import com.example.demo.entity.user.User;
import com.example.demo.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsById(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.role = :role WHERE u.userId = :userId")
    int updateUserRole(@Param("userId") UUID userId, @Param("role") UserRole role);

    @Modifying
    @Query("DELETE FROM User u WHERE u.login = :login AND u.userId != :currentUserId")
    int deleteByLogin(@Param("login") String login, @Param("currentUserId") UUID currentUserId);
}
