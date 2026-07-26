package org.example.controller;

import org.example.dto.BaseResponse;
import org.example.dto.requestDTO.ApproveExpenseRequest;
import org.example.dto.responseDTO.ApprovalResult;
import org.example.dto.responseDTO.ApproveExpenseResponse;
import org.example.dto.responseDTO.ValidateTokenResponse;
import org.example.service.ApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/approval")
public class ApprovalController {
    private final ApprovalService approvalService;

    // private final ExpenseResponse expenseResponse;
    // private final ExpenseClient expenseClient;
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;

    }

    @GetMapping
public BaseResponse<List<ApprovalResult>> getApprovals(
        @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    List<ApprovalResult> approvals = approvalService.getApprovals(token);

    return new BaseResponse<>(
            approvals,
            "Approvals fetched successfully.");
}

    @PostMapping("/initiate/{expenseId}")
    public BaseResponse<String> initiate(@PathVariable UUID expenseId) {

        approvalService.createInitialApproval(expenseId);

        return new BaseResponse<>(
                "Approval initiated",
                "Manager approval created");
    }

    @PostMapping("/approve")
    public BaseResponse<ApproveExpenseResponse> approve(@RequestBody ApproveExpenseRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        // ValidateTokenResponse user = approvalService.validateUser(token);
        // System.out.println(user.getEmail());
        // System.out.println(user.getRole());

        ApprovalResult approved = approvalService.approve(request.getId(),
                token);

        // ExpenseResponse expense = expenseClient.getExpense(request.getId());
        return new BaseResponse<>(
                new ApproveExpenseResponse(
                        approved.getApproval().getId(),
                        approved.getApproval().getStatus(),
                        approved.getExpenseStatus(),
                        LocalDateTime.now()),
                "Expense approved successfully");
    }

    @PostMapping("/reject")
    public BaseResponse<ApproveExpenseResponse> reject(@RequestBody ApproveExpenseRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        ApprovalResult approved = approvalService.reject(request.getId(), token);
        // ExpenseResponse expense = expenseClient.getExpense(request.getId());
        return new BaseResponse<>(
                new ApproveExpenseResponse(
                        approved.getApproval().getId(),
                        approved.getApproval().getStatus(),
                        approved.getExpenseStatus(),
                        LocalDateTime.now()),
                "Expense rejected.");
    }

}
