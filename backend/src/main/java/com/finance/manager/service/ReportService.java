package com.finance.manager.service;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.TransactionType;
import com.finance.manager.model.User;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final TransactionRepository transactionRepository;
    
    public Map<String, Object> getMonthlyReport(int year, int month, User user) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(
            user, startDate, endDate
        );
        
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
            } else {
                totalExpenses.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
            }
        }
        
        BigDecimal totalIncomeAmount = totalIncome.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalExpenseAmount = totalExpenses.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal netSavings = totalIncomeAmount.subtract(totalExpenseAmount);
        
        Map<String, Object> report = new HashMap<>();
        report.put("month", month);
        report.put("year", year);
        report.put("totalIncome", totalIncome);
        report.put("totalExpenses", totalExpenses);
        report.put("netSavings", netSavings);
        
        return report;
    }
    
    public Map<String, Object> getYearlyReport(int year, User user) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(
            user, startDate, endDate
        );
        
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
            } else {
                totalExpenses.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
            }
        }
        
        BigDecimal totalIncomeAmount = totalIncome.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalExpenseAmount = totalExpenses.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal netSavings = totalIncomeAmount.subtract(totalExpenseAmount);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("totalIncome", totalIncome);
        report.put("totalExpenses", totalExpenses);
        report.put("netSavings", netSavings);
        
        return report;
    }
}
