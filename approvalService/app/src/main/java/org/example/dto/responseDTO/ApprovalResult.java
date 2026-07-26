package org.example.dto.responseDTO;

import org.example.entity.Approval;
import org.example.enums.ExpenseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalResult {
    private Approval approval;
    private ExpenseStatus expenseStatus;

    
}