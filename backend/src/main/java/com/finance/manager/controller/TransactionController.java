package com.finance.manager.controller;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.dto.TransactionUpdateRequest;
import com.finance.manager.model.TransactionType;
import com.finance.manager.model.User;
import com.finance.manager.service.AuthService;
import com.finance.manager.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    private final AuthService authService;
    
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        User currentUser = authService.getCurrentUser();
        TransactionResponse response = transactionService.createTransaction(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TransactionType type) {
        
        User currentUser = authService.getCurrentUser();
        List<TransactionResponse> transactions = transactionService.getTransactions(
            currentUser, startDate, endDate, category, type
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactions);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request) {
        
        User currentUser = authService.getCurrentUser();
        TransactionResponse response = transactionService.updateTransaction(id, request, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTransaction(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        transactionService.deleteTransaction(id, currentUser);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transaction deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}
