package com.finance.manager.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalUpdateRequest {
    
    @Positive(message = "Target amount must be positive")
    private BigDecimal targetAmount;
    
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
}
