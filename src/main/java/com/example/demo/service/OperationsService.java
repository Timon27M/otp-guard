package com.example.demo.service;

import com.example.demo.entity.Operation;
import com.example.demo.entity.user.User;
import com.example.demo.repository.OperationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationsService {
    private final OperationRepository operationRepository;

    public Operation createOperation(String operationName, User user) {
        Operation operation = new Operation(operationName, user);

        return operationRepository.save(operation);
    }
}
