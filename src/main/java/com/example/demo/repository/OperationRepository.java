package com.example.demo.repository;

import com.example.demo.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OperationRepository extends JpaRepository<Operation, UUID> {

}
