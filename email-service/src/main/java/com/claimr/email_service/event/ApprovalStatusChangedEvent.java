package com.claimr.email_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class ApprovalStatusChangedEvent {

    private UUID expenseId;
    private UUID employeeId;
    private String employeeEmail;
    private String newStatus;
    private String approvalLevel;
    private String remarks;
    private LocalDateTime timestamp;    
}