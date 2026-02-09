package com.finance.manager.service;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.dto.CategoryResponse;
import com.finance.manager.exception.BadRequestException;
import com.finance.manager.exception.DuplicateResourceException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.model.Category;
import com.finance.manager.model.TransactionType;
import com.finance.manager.model.User;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    
    private static final List<String> DEFAULT_INCOME_CATEGORIES = Arrays.asList("Salary");
    private static final List<String> DEFAULT_EXPENSE_CATEGORIES = Arrays.asList(
        "Food", "Rent", "Transportation", "Entertainment", "Healthcare", "Utilities"
    );
    
    @Transactional
    public void initializeDefaultCategories(User user) {
        // Check if default categories exist, if not create them
        List<Category> defaultCategories = categoryRepository.findByUserIsNull();
        
        if (defaultCategories.isEmpty()) {
            List<Category> categories = new ArrayList<>();
            
            for (String name : DEFAULT_INCOME_CATEGORIES) {
                categories.add(new Category(name, TransactionType.INCOME, false));
            }
            
            for (String name : DEFAULT_EXPENSE_CATEGORIES) {
                categories.add(new Category(name, TransactionType.EXPENSE, false));
            }
            
            categoryRepository.saveAll(categories);
        }
    }
    
    public List<CategoryResponse> getAllCategories(User user) {
        List<CategoryResponse> responses = new ArrayList<>();
        
        // Get default categories
        List<Category> defaultCategories = categoryRepository.findByUserIsNull();
        responses.addAll(defaultCategories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList()));
        
        // Get user's custom categories
        List<Category> customCategories = categoryRepository.findByUser(user);
        responses.addAll(customCategories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList()));
        
        return responses;
    }
    
    @Transactional
    public CategoryResponse createCustomCategory(CategoryRequest request, User user) {
        // Check if category with same name already exists for user
        if (categoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new DuplicateResourceException("Category with this name already exists");
        }
        
        // Check if default category with same name exists
        if (categoryRepository.findByNameAndUserIsNull(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Category with this name already exists as a default category");
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setCustom(true);
        category.setUser(user);
        
        Category saved = categoryRepository.save(category);
        return convertToResponse(saved);
    }
    
    @Transactional
    public void deleteCustomCategory(String name, User user) {
        Category category = categoryRepository.findByNameAndUser(name, user)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        if (!category.isCustom()) {
            throw new BadRequestException("Cannot delete default categories");
        }
        
        // Check if category is being used by any transactions
        long count = transactionRepository.countByUserAndCategory(user, name);
        if (count > 0) {
            throw new BadRequestException("Cannot delete category that is referenced by transactions");
        }
        
        categoryRepository.delete(category);
    }
    
    public boolean isValidCategory(String categoryName, User user) {
        // Check if it's a default category
        if (categoryRepository.findByNameAndUserIsNull(categoryName).isPresent()) {
            return true;
        }
        
        // Check if it's a user's custom category
        return categoryRepository.findByNameAndUser(categoryName, user).isPresent();
    }
    
    public TransactionType getCategoryType(String categoryName, User user) {
        // Check default categories first
        Category defaultCategory = categoryRepository.findByNameAndUserIsNull(categoryName).orElse(null);
        if (defaultCategory != null) {
            return defaultCategory.getType();
        }
        
        // Check custom categories
        Category customCategory = categoryRepository.findByNameAndUser(categoryName, user)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        return customCategory.getType();
    }
    
    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(
            category.getName(),
            category.getType(),
            category.isCustom()
        );
    }
}
