package org.example.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.ExpenseStatus;

import java.time.LocalDateTime;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitExpenseResponse {
    private UUID id;
    private ExpenseStatus status;
    private LocalDateTime createdAt;

}
