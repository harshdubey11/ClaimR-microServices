package org.example.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStatusChangedEvent {

    private UUID expenseId;
    private UUID employeeId;
    private String employeeEmail;
    private String newStatus;
    private String approvalLevel;
    private String remarks;
    private LocalDateTime timestamp;

}
