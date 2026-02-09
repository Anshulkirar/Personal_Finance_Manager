package com.finance.manager.controller;

import com.finance.manager.dto.SavingsGoalRequest;
import com.finance.manager.dto.SavingsGoalResponse;
import com.finance.manager.dto.SavingsGoalUpdateRequest;
import com.finance.manager.model.User;
import com.finance.manager.service.AuthService;
import com.finance.manager.service.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class SavingsGoalController {
    
    private final SavingsGoalService savingsGoalService;
    private final AuthService authService;
    
    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(@Valid @RequestBody SavingsGoalRequest request) {
        User currentUser = authService.getCurrentUser();
        SavingsGoalResponse response = savingsGoalService.createGoal(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGoals() {
        User currentUser = authService.getCurrentUser();
        List<SavingsGoalResponse> goals = savingsGoalService.getAllGoals(currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("goals", goals);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getGoal(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        SavingsGoalResponse response = savingsGoalService.getGoal(id, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalUpdateRequest request) {
        
        User currentUser = authService.getCurrentUser();
        SavingsGoalResponse response = savingsGoalService.updateGoal(id, request, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        savingsGoalService.deleteGoal(id, currentUser);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Goal deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}
