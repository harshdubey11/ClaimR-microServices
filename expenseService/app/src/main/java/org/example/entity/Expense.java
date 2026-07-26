package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.enums.ExpenseStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "expenses")
public class Expense {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "department_id")
    private UUID departmentId;

    private double amount;

    private String category;

    private String description;

    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Expense orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }

    // Getters & Setters
}