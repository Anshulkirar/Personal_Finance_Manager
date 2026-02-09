package com.finance.manager.service;

import com.finance.manager.dto.SavingsGoalRequest;
import com.finance.manager.dto.SavingsGoalResponse;
import com.finance.manager.dto.SavingsGoalUpdateRequest;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.model.SavingsGoal;
import com.finance.manager.model.Transaction;
import com.finance.manager.model.TransactionType;
import com.finance.manager.model.User;
import com.finance.manager.repository.SavingsGoalRepository;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {
    
    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;
    
    @Transactional
    public SavingsGoalResponse createGoal(SavingsGoalRequest request, User user) {
        SavingsGoal goal = new SavingsGoal();
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
        goal.setUser(user);
        
        SavingsGoal saved = savingsGoalRepository.save(goal);
        return convertToResponse(saved, user);
    }
    
    public List<SavingsGoalResponse> getAllGoals(User user) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUser(user);
        return goals.stream()
            .map(goal -> convertToResponse(goal, user))
            .collect(Collectors.toList());
    }
    
    public SavingsGoalResponse getGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        
        return convertToResponse(goal, user);
    }
    
    @Transactional
    public SavingsGoalResponse updateGoal(Long id, SavingsGoalUpdateRequest request, User user) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        
        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }
        
        SavingsGoal updated = savingsGoalRepository.save(goal);
        return convertToResponse(updated, user);
    }
    
    @Transactional
    public void deleteGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        
        savingsGoalRepository.delete(goal);
    }
    
    private SavingsGoalResponse convertToResponse(SavingsGoal goal, User user) {
        BigDecimal currentProgress = calculateProgress(goal, user);
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress);
        
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }
        
        double progressPercentage = 0.0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = currentProgress
                .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
            
            if (progressPercentage > 100.0) {
                progressPercentage = 100.0;
            }
        }
        
        return new SavingsGoalResponse(
            goal.getId(),
            goal.getGoalName(),
            goal.getTargetAmount(),
            goal.getTargetDate(),
            goal.getStartDate(),
            currentProgress,
            Math.round(progressPercentage * 100.0) / 100.0,
            remainingAmount
        );
    }
    
    private BigDecimal calculateProgress(SavingsGoal goal, User user) {
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(
            user, goal.getStartDate(), LocalDate.now()
        );
        
        BigDecimal totalIncome = transactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalExpenses = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalIncome.subtract(totalExpenses);
    }
}
