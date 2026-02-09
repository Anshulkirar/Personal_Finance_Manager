package com.finance.manager.controller;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.dto.CategoryResponse;
import com.finance.manager.model.User;
import com.finance.manager.service.AuthService;
import com.finance.manager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        User currentUser = authService.getCurrentUser();
        List<CategoryResponse> categories = categoryService.getAllCategories(currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<CategoryResponse> createCustomCategory(@Valid @RequestBody CategoryRequest request) {
        User currentUser = authService.getCurrentUser();
        CategoryResponse response = categoryService.createCustomCategory(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> deleteCustomCategory(@PathVariable String name) {
        User currentUser = authService.getCurrentUser();
        categoryService.deleteCustomCategory(name, currentUser);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Category deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}
