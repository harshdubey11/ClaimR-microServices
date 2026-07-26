package org.example.dto.requestDTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateExpenseRequest {

    private Double amount;
    private String description;
    private String category;
    private UUID departmentId;

  
}