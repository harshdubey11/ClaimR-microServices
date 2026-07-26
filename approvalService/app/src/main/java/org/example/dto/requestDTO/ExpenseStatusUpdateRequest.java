package org.example.dto.requestDTO;

import org.example.enums.ExpenseStatus;



public class ExpenseStatusUpdateRequest {
    private ExpenseStatus status;

    public ExpenseStatusUpdateRequest(){}

    public ExpenseStatusUpdateRequest(ExpenseStatus status) {
        this.status = status;
    }

    public ExpenseStatus getStatus() {
        return status;
    }
}