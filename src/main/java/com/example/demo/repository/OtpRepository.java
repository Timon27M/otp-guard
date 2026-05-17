package com.example.demo.repository;

import com.example.demo.entity.OtpData.OtpData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<OtpData, UUID> {

    @Modifying
    @Query("UPDATE OtpData o SET o.status = 'EXPIRED' WHERE o.status = 'ACTIVE' AND o.expiresAt < CURRENT_TIMESTAMP")
    int expireOldCodes();

    Optional<OtpData> findByOperationId(UUID  operationId);
}
