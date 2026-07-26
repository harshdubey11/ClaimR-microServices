package org.example.service;

import jakarta.transaction.Transactional;

import org.example.client.ApprovalClient;
import org.example.client.AuthClient;
import org.example.dto.BaseResponse;
import org.example.dto.requestDTO.UpdateExpenseRequest;
import org.example.dto.requestDTO.ValidateTokenRequest;
import org.example.dto.responseDTO.ValidateTokenResponse;
import org.example.entity.Expense;
import org.example.enums.ExpenseStatus;
import org.example.exception.ExpenseNotFoundException;
import org.example.exception.InvalidApprovalException;

import org.example.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import feign.FeignException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ApprovalClient approvalClient;
    private final AuthClient authClient;
    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);

    public ExpenseService(ExpenseRepository expenseRepository,
            ApprovalClient approvalClient,
            AuthClient authClient) {
        this.expenseRepository = expenseRepository;
        this.approvalClient = approvalClient;
        this.authClient = authClient;
    }

    private ValidateTokenResponse validateUser(String token) {

        BaseResponse<ValidateTokenResponse> response = authClient.validate(
                new ValidateTokenRequest(token));

        if (response == null ||
                response.getData() == null ||
                !response.getData().isValid()) {
            throw new RuntimeException("Invalid token");
        }

        return response.getData();
    }

    public Expense updateExpense(UUID expenseId,
            UpdateExpenseRequest request,
            String token) {

        ValidateTokenResponse user = validateUser(token);

        if (!"EMPLOYEE".equals(user.getRole())) {
            throw new RuntimeException("Only EMPLOYEE can update expenses");
        }

        Expense expense = getExpenseById(expenseId);

        if (!expense.getUserId().equals(user.getUserId())) {
            throw new RuntimeException(
                    "You can only update your own expenses.");
        }

        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            throw new RuntimeException(
                    "Only DRAFT expenses can be updated.");
        }

        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setCategory(request.getCategory());
        expense.setDepartmentId(request.getDepartmentId());

        return expenseRepository.save(expense);
    }

    public void deleteExpense(UUID expenseId, String token) {

        ValidateTokenResponse user = validateUser(token);

        if (!"EMPLOYEE".equals(user.getRole())) {
            throw new RuntimeException("Only EMPLOYEE can delete expenses");
        }

        Expense expense = getExpenseById(expenseId);

        if (!expense.getUserId().equals(user.getUserId())) {
            throw new RuntimeException(
                    "You can only delete your own expenses.");
        }

        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            throw new RuntimeException(
                    "Only DRAFT expenses can be deleted.");
        }

        expenseRepository.delete(expense);

        log.info(
                "Expense {} deleted by {}",
                expenseId,
                user.getEmail());
    }

    public Expense createExpense(Expense expense, String token) {
        ValidateTokenResponse user = validateUser(token);

        if (!"EMPLOYEE".equals(user.getRole())) {
            throw new RuntimeException("Only EMPLOYEE can create expenses");
        }

        log.info("Expense creation requested by {}", user.getEmail());
        expense.setId(UUID.randomUUID());
        expense.setStatus(ExpenseStatus.DRAFT);
        expense.setUserId(user.getUserId());
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getMyExpenses(String token) {
        ValidateTokenResponse user = validateUser(token);

        return expenseRepository.findAllByUserId(user.getUserId());
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Expense submitExpense(@NonNull UUID expenseId, String token) {
        ValidateTokenResponse user = validateUser(token);
        if (!"EMPLOYEE".equals(user.getRole())) {
            throw new RuntimeException("Only EMPLOYEE can submit expenses");
        }
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found!"));

        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            throw new InvalidApprovalException("Only DRAFT expenses can be submitted");
        }
        if (!expense.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You can only submit your own expenses.");
        }
        // Update status
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseRepository.save(expense);

        // Create approval entry (MANAGER level)
        try {
            log.info("Calling approval service for expense {}", expenseId);
            approvalClient.initiateApproval(expenseId);
        } catch (Exception ex) {

            log.warn("Approval creation failed for expense {}", expenseId);
            throw new RuntimeException("Failed to create approval. Expense submission aborted.", ex);
        }

        return expense;
    }

    @Recover
    public Expense recover(Exception ex, UUID expenseId) {

        log.error(
                "Approval service unavailable after retries for expense {}",
                expenseId,
                ex);

        throw new RuntimeException(
                "Approval service unavailable after retries. Submission failed.");
    }

    public Expense getExpenseById(@NonNull UUID expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found!"));
    }

    public Expense updateExpenseStatus(@NonNull UUID expenseId, ExpenseStatus status) {
        Expense expense = getExpenseById(expenseId);
        expense.setStatus(status);
        expenseRepository.save(expense);
        return expense;
    }
}
