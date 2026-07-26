package org.example.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.ExpenseStatus;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponse {
    private UUID id;
    private ExpenseStatus status;
    private double amount;
    private UUID departmentId;
    private String category;
    private String description;
}
