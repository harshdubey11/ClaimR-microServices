package org.example.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users")
public class User{
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(nullable = false, updatable = false)
private UUID id;

    private String name;

    @Column(unique = true,name = "email",nullable = false)
    private String email;

    @Column(name="password_hash",nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    @Column(name="department_id")
    private String departmentId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
