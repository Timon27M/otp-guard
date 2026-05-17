package com.example.demo.entity;

import com.example.demo.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "operations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "operation_id", updatable = false, nullable = false, unique = true)
    @Getter
    @Setter
    UUID operationId;

    @Column(name = "name", length = 100)
    @Getter
    @Setter
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Getter
    @Setter
    User user;

    public Operation(String name, User user) {
        this.name = name;
        this.user = user;
    }
}