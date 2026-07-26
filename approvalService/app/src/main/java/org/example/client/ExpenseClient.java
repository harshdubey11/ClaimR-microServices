package org.example.client;

import org.example.dto.requestDTO.ExpenseStatusUpdateRequest;
import org.example.dto.responseDTO.ExpenseResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@FeignClient(
        name = "expense-service",
        url = "${expense.service.url}"
)
public interface ExpenseClient {

    @GetMapping("/expenses/{id}")
    ExpenseResponse getExpense(@PathVariable("id") UUID id);

    @PutMapping(value = "/expenses/{id}/status", consumes = "application/json")
    void updateExpenseStatus(@PathVariable("id") UUID id,
                             @RequestBody ExpenseStatusUpdateRequest request);

 
}