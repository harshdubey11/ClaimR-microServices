package org.example.exception;

public class InvalidApprovalException extends RuntimeException {
    public InvalidApprovalException(String message) {
        super(message);
    }
}