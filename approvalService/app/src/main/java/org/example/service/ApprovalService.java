package org.example.service;

import jakarta.transaction.Transactional;

import org.example.client.AuthClient;
import org.example.client.ExpenseClient;
import org.example.dto.BaseResponse;
import org.example.dto.requestDTO.ExpenseStatusUpdateRequest;
import org.example.dto.requestDTO.ValidateTokenRequest;
import org.example.dto.responseDTO.ApprovalResult;
import org.example.dto.responseDTO.ExpenseResponse;
import org.example.dto.responseDTO.ValidateTokenResponse;
import org.example.entity.Approval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.enums.ApprovalLevel;
import org.example.enums.ApprovalStatus;
import org.example.enums.ExpenseStatus;
import org.example.event.ApprovalStatusChangedEvent;
import org.example.exception.InvalidApprovalException;
import org.example.repository.ApprovalRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;

@Service
@Transactional
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final ExpenseClient expenseClient;
    private final AuthClient authClient;
    private final ApprovalEventProducer approvalEventProducer;
    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    public ApprovalService(ApprovalRepository approvalRepository,
            ExpenseClient expenseClient,
            AuthClient authClient,
            ApprovalEventProducer approvalEventProducer) {
        this.approvalRepository = approvalRepository;
        this.expenseClient = expenseClient;
        this.authClient = authClient;
        this.approvalEventProducer = approvalEventProducer;
    }

    private void publishApprovalEvent(
            ExpenseResponse expense,
            Approval approval,
            ExpenseStatus newStatus) {

        ApprovalStatusChangedEvent event = new ApprovalStatusChangedEvent(
                expense.getId(),
                expense.getEmployeeId(),
                expense.getEmployeeEmail(),
                newStatus.name(),
                approval.getLevel().name(),
                null,
                approval.getActionTime());

        approvalEventProducer.publish(event);
    }

    private ValidateTokenResponse validateUser(String token) {

        BaseResponse<ValidateTokenResponse> response = authClient.validate(
                new ValidateTokenRequest(token));

        if (!response.getData().isValid()) {
            throw new RuntimeException("Invalid token");
        }

        return response.getData();
    }

    public void createInitialApproval(UUID expenseId) {
        // prevent duplicate approvals
        List<Approval> existing = approvalRepository
                .findAllByExpenseIdAndStatus(expenseId, ApprovalStatus.PENDING);

        if (!existing.isEmpty()) {
            throw new RuntimeException("Approval already exists for this expense");
        }

        Approval approval = new Approval();
        approval.setId(UUID.randomUUID());
        approval.setExpenseId(expenseId);
        approval.setStatus(ApprovalStatus.PENDING);
        approval.setLevel(ApprovalLevel.MANAGER);
        approvalRepository.save(approval);
    }

    private ExpenseResponse getExpenseOrThrow(UUID expenseId) {
        ExpenseResponse expense = expenseClient.getExpense(expenseId);
        if (expense == null) {
            throw new InvalidApprovalException("Expense not found");
        }
        return expense;

    }

    public ApprovalResult reject(UUID expenseId, String token) {

        List<Approval> approvals = approvalRepository
                .findAllByExpenseIdAndStatus(expenseId, ApprovalStatus.PENDING);

        if (approvals.isEmpty()) {
            throw new InvalidApprovalException("No pending approval found");
        }

        if (approvals.size() > 1) {
            throw new InvalidApprovalException("Multiple pending approvals found");
        }

        Approval approval = approvals.getFirst();
        ValidateTokenResponse user = validateUser(token);

        return switch (approval.getLevel()) {
            case MANAGER -> rejectByManager(approval, expenseId, user);
            case FINANCE -> rejectByFinance(approval, expenseId, user);
            default -> throw new InvalidApprovalException("Unsupported approval level");
        };
    }

    public ApprovalResult approve(UUID expenseId, String token) {

        List<Approval> approvals = approvalRepository
                .findAllByExpenseIdAndStatus(expenseId, ApprovalStatus.PENDING);
        ValidateTokenResponse user = validateUser(token);
        if (approvals.isEmpty()) {
            throw new InvalidApprovalException("No pending approval found");
        }

        if (approvals.size() > 1) {
            throw new InvalidApprovalException("Multiple pending approvals found");
        }

        Approval approval = approvals.getFirst();

        return switch (approval.getLevel()) {
            case MANAGER -> approveByManager(approval, expenseId, user);
            case FINANCE -> approveByFinance(approval, expenseId, user);
            default -> throw new InvalidApprovalException("Unsupported approval level");
        };
    }

    public ApprovalResult approveByManager(Approval approval, UUID expenseId, ValidateTokenResponse user) {

        ExpenseResponse expense = getExpenseOrThrow(expenseId);

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new InvalidApprovalException("Expense not ready for manager approval");
        }
        // Step 1: Update current approval
        if (!"MANAGER".equals(user.getRole())) {
            throw new InvalidApprovalException(
                    "Only MANAGER can perform this action");
        }

        log.info("Manager approval requested by {}", user.getEmail());

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setApproverId(user.getUserId());
        approval.setActionTime(LocalDateTime.now());
        approvalRepository.save(approval);

        // Step 2: Update expense status

        // Step 3: Create FINANCE approval
        Approval financeApproval = new Approval();
        financeApproval.setId(UUID.randomUUID());
        financeApproval.setExpenseId(expenseId);
        financeApproval.setLevel(ApprovalLevel.FINANCE);
        financeApproval.setStatus(ApprovalStatus.PENDING);
        financeApproval.setApproverId(null);
        financeApproval.setActionTime(null);

        // updating expense status
        expenseClient.updateExpenseStatus(expenseId,
                new ExpenseStatusUpdateRequest(ExpenseStatus.MANAGER_APPROVED));

        approvalRepository.save(financeApproval);

        publishApprovalEvent(
                expense, approval, ExpenseStatus.MANAGER_APPROVED);
        return new ApprovalResult(
                approval,
                ExpenseStatus.MANAGER_APPROVED);

    }

    public ApprovalResult rejectByManager(Approval approval, UUID expenseId, ValidateTokenResponse user) {

        ExpenseResponse expense = getExpenseOrThrow(expenseId);
        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new InvalidApprovalException("Expense not ready for manager rejection");
        }
        if (!"MANAGER".equals(user.getRole())) {
            throw new InvalidApprovalException(
                    "Only MANAGER can perform this action");
        }

        log.info("Manager rejection requested by {}", user.getEmail());
        // Update current approval
        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setApproverId(user.getUserId());
        approval.setActionTime(LocalDateTime.now());

        // Update Expense status
        expenseClient.updateExpenseStatus(expenseId,
                new ExpenseStatusUpdateRequest(ExpenseStatus.REJECTED));

        approvalRepository.save(approval);

        publishApprovalEvent(
                expense, approval, ExpenseStatus.REJECTED);
        return new ApprovalResult(
                approval,
                ExpenseStatus.REJECTED);

    }

    public ApprovalResult approveByFinance(Approval approval, UUID expenseId, ValidateTokenResponse user) {

        ExpenseResponse expense = getExpenseOrThrow(expenseId);

        if (expense.getStatus() != ExpenseStatus.MANAGER_APPROVED) {
            throw new InvalidApprovalException("Expense not ready for finance approval");
        }

        if (!"FINANCE".equals(user.getRole())) {
            throw new InvalidApprovalException(
                    "Only FINANCE can perform this action");
        }

        log.info("Finance approval requested by {}", user.getEmail());

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setApproverId(user.getUserId());
        approval.setActionTime(LocalDateTime.now());
        expenseClient.updateExpenseStatus(expenseId,
                new ExpenseStatusUpdateRequest(ExpenseStatus.FINANCE_APPROVED));

        approvalRepository.save(approval);

        publishApprovalEvent(
                expense, approval, ExpenseStatus.FINANCE_APPROVED);
        return new ApprovalResult(
                approval,
                ExpenseStatus.FINANCE_APPROVED);
    }

    public ApprovalResult rejectByFinance(Approval approval, UUID expenseId, ValidateTokenResponse user) {

        ExpenseResponse expense = getExpenseOrThrow(expenseId);

        if (expense.getStatus() != ExpenseStatus.MANAGER_APPROVED) {
            throw new InvalidApprovalException("Expense not ready for finance rejection");
        }
        if (!"FINANCE".equals(user.getRole())) {
            throw new InvalidApprovalException(
                    "Only FINANCE can perform this action");
        }

        log.info("Finance rejection requested by {}", user.getEmail());
        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setApproverId(user.getUserId());
        approval.setActionTime(LocalDateTime.now());
        expenseClient.updateExpenseStatus(expenseId,
                new ExpenseStatusUpdateRequest(ExpenseStatus.REJECTED));

        approvalRepository.save(approval);

        publishApprovalEvent(
                expense, approval, ExpenseStatus.REJECTED);
        return new ApprovalResult(
                approval,
                ExpenseStatus.REJECTED);
    }

    public List<ApprovalResult> getApprovals(String token) {

        ValidateTokenResponse user = validateUser(token);

        List<Approval> approvals;

        if ("MANAGER".equals(user.getRole())) {

            approvals = approvalRepository.findAllByLevel(ApprovalLevel.MANAGER);

        } else if ("FINANCE".equals(user.getRole())) {

            approvals = approvalRepository.findAllByLevel(ApprovalLevel.FINANCE);

        } else {

            throw new InvalidApprovalException(
                    "Only MANAGER and FINANCE can view approvals.");
        }

        if (approvals.isEmpty()) {
            throw new InvalidApprovalException("No approvals found.");
        }

        return approvals.stream()
                .map(approval -> {

                    ExpenseStatus expenseStatus = expenseClient
                            .getExpense(approval.getExpenseId())
                            .getStatus();

                    return new ApprovalResult(
                            approval,
                            expenseStatus);
                })
                .toList();
    }

}
