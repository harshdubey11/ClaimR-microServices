package org.example.dto.requestDTO;

import org.example.enums.ExpenseStatus;


public class ExpenseStatusUpdateRequest  {

    private ExpenseStatus status;

    public ExpenseStatusUpdateRequest () {}   

    public ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }
}