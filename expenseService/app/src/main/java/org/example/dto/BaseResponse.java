package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private T data;
    private String message;
    private LocalDateTime timestamp;

    public BaseResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}