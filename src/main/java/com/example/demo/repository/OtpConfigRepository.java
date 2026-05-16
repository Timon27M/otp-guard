package com.example.demo.repository;

import com.example.demo.entity.OtpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface OtpConfigRepository extends JpaRepository<OtpConfig, Integer> {

    Optional<OtpConfig> findOtpConfigByConfigId(Integer configId);
}
