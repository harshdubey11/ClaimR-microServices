package org.example.util;

import org.example.dto.ErrorResponse;
import org.example.exception.ExpenseNotFoundException;
import org.example.exception.InvalidApprovalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExpenseNotFound(ExpenseNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidApprovalException.class)
    public ResponseEntity<ErrorResponse> handleInvalidApproval(InvalidApprovalException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}