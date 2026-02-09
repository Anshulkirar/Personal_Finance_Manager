package com.finance.manager.service;

import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.exception.DuplicateResourceException;
import com.finance.manager.model.User;
import com.finance.manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private CategoryService categoryService;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("+1234567890");
    }
    
    @Test
    void register_Success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(registerRequest.getUsername());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(categoryService).initializeDefaultCategories(any(User.class));
        
        Long userId = authService.register(registerRequest);
        
        assertNotNull(userId);
        assertEquals(1L, userId);
        verify(userRepository).save(any(User.class));
        verify(categoryService).initializeDefaultCategories(any(User.class));
    }
    
    @Test
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
