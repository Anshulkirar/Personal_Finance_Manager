package com.finance.manager.service;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.dto.TransactionUpdateRequest;
import com.finance.manager.exception.BadRequestException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.model.Transaction;
import com.finance.manager.model.TransactionType;
import com.finance.manager.model.User;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        // Validate category
        if (!categoryService.isValidCategory(request.getCategory(), user)) {
            throw new BadRequestException("Invalid category");
        }
        
        TransactionType type = categoryService.getCategoryType(request.getCategory(), user);
        
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setType(type);
        transaction.setUser(user);
        
        Transaction saved = transactionRepository.save(transaction);
        return convertToResponse(saved);
    }
    
    public List<TransactionResponse> getTransactions(User user, LocalDate startDate, LocalDate endDate, 
                                                     String category, TransactionType type) {
        List<Transaction> transactions;
        
        if (startDate != null || endDate != null || category != null || type != null) {
            transactions = transactionRepository.findByFilters(user, startDate, endDate, category, type);
        } else {
            transactions = transactionRepository.findByUserOrderByDateDesc(user);
        }
        
        return transactions.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        
        if (request.getCategory() != null) {
            if (!categoryService.isValidCategory(request.getCategory(), user)) {
                throw new BadRequestException("Invalid category");
            }
            TransactionType type = categoryService.getCategoryType(request.getCategory(), user);
            transaction.setCategory(request.getCategory());
            transaction.setType(type);
        }
        
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        
        Transaction updated = transactionRepository.save(transaction);
        return convertToResponse(updated);
    }
    
    @Transactional
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        transactionRepository.delete(transaction);
    }
    
    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getDate(),
            transaction.getCategory(),
            transaction.getDescription(),
            transaction.getType()
        );
    }
}
