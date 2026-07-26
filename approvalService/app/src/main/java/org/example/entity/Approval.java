package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.enums.ApprovalLevel;
import org.example.enums.ApprovalStatus;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "approvals")
public class Approval {

    @Id
    private UUID id;

    @Column(name = "expense_id")
    private UUID expenseId;

    @Column(name = "approver_id")
    private UUID approverId;

    @Enumerated(EnumType.STRING)
    private ApprovalLevel level;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private String remarks;

    private LocalDateTime actionTime;
}