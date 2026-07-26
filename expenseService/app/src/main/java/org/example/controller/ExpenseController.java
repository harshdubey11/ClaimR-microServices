package org.example.controller;

import org.example.dto.BaseResponse;
import org.example.dto.requestDTO.CreateExpenseRequest;
import org.example.dto.requestDTO.SubmitExpenseRequest;
import org.example.dto.requestDTO.UpdateExpenseRequest;
import org.example.dto.requestDTO.ExpenseStatusUpdateRequest;
import org.example.dto.responseDTO.ExpenseResponse;
import org.example.dto.responseDTO.SubmitExpenseResponse;
import org.example.entity.Expense;
import org.example.service.ExpenseService;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
        private final ExpenseService expenseService;

        public ExpenseController(ExpenseService expenseService) {
                this.expenseService = expenseService;
        }

        @GetMapping("/{id}")
        public ExpenseResponse findExpense(@PathVariable @NonNull UUID id) {
                Expense expense = expenseService.getExpenseById(id);
                return new ExpenseResponse(
                                expense.getId(),
                                expense.getStatus(),
                                expense.getAmount(),
                                expense.getDepartmentId(),
                                expense.getCategory(),
                                expense.getDescription());
        }

        @PutMapping("/{id}/status")
        public ExpenseResponse updateExpense(@PathVariable @NonNull UUID id,
                        @RequestBody ExpenseStatusUpdateRequest request) {
                Expense expense = expenseService.updateExpenseStatus(id, request.getStatus());
                System.out.println("Status update called: " + request.getStatus());
                return new ExpenseResponse(
                                expense.getId(),
                                expense.getStatus(),
                                expense.getAmount(),
                                expense.getDepartmentId(),
                                expense.getCategory(),
                                expense.getDescription());
        }

        @PostMapping
        public BaseResponse<ExpenseResponse> createExpense(@RequestBody CreateExpenseRequest request,
                        @RequestHeader("Authorization") String authHeader) {
                String token = authHeader.replace("Bearer ", "");
                Expense expense = new Expense();
                expense.setDescription(request.getDescription());
                expense.setAmount(request.getAmount());
                expense.setCategory(request.getCategory());
                expense.setDepartmentId(request.getDepartmentId());
                Expense saved = expenseService.createExpense(expense, token);
                return new BaseResponse<>(
                                new ExpenseResponse(
                                                saved.getId(),
                                                saved.getStatus(),
                                                saved.getAmount(),
                                                saved.getDepartmentId(),
                                                saved.getCategory(),
                                                saved.getDescription()

                                ), "Expense created successfully.");
        }

        @PostMapping("/submit")
        public BaseResponse<SubmitExpenseResponse> submitExpense(@RequestBody SubmitExpenseRequest request,
                        @RequestHeader("Authorization") String authHeader) {
                String token = authHeader.replace("Bearer ", "");
                Expense submitted = expenseService.submitExpense(request.getId(), token);
                return new BaseResponse<>(
                                new SubmitExpenseResponse(

                                                submitted.getId(),
                                                submitted.getStatus(),
                                                LocalDateTime.now()),
                                "Expense Submitted Successfully.");

        }

        @GetMapping("/my")
        public BaseResponse<List<ExpenseResponse>> getMyExpenses(
                        @RequestHeader("Authorization") String authHeader) {

                String token = authHeader.replace("Bearer ", "");

                List<ExpenseResponse> expenses = expenseService
                                .getMyExpenses(token)
                                .stream()
                                .map(expense -> new ExpenseResponse(
                                                expense.getId(),
                                                expense.getStatus(),
                                                expense.getAmount(),
                                                expense.getDepartmentId(),
                                                expense.getCategory(),
                                                expense.getDescription()))
                                .toList();

                return new BaseResponse<>(expenses, "Expenses fetched successfully.");
        }

        @PutMapping("/{id}")
        public BaseResponse<ExpenseResponse> updateExpense(
                        @PathVariable UUID id,
                        @RequestBody UpdateExpenseRequest request,
                        @RequestHeader("Authorization") String authHeader) {

                String token = authHeader.replace("Bearer ", "");

                Expense expense = expenseService.updateExpense(id, request, token);

                return new BaseResponse<>(
                                new ExpenseResponse(
                                                expense.getId(),
                                                expense.getStatus(),
                                                expense.getAmount(),
                                                expense.getDepartmentId(),
                                                expense.getCategory(),
                                                expense.getDescription()),
                                "Expense updated successfully.");
        }

        @DeleteMapping("/{id}")
        public BaseResponse<Void> deleteExpense(
                        @PathVariable UUID id,
                        @RequestHeader("Authorization") String authHeader) {

                String token = authHeader.replace("Bearer ", "");

                expenseService.deleteExpense(id, token);

                return new BaseResponse<>(
                                null,
                                "Expense deleted successfully.");
        }
}
