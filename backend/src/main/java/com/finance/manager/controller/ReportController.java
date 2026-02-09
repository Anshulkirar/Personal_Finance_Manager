package com.finance.manager.controller;

import com.finance.manager.model.User;
import com.finance.manager.service.AuthService;
import com.finance.manager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    private final AuthService authService;
    
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {
        
        User currentUser = authService.getCurrentUser();
        Map<String, Object> report = reportService.getMonthlyReport(year, month, currentUser);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/yearly/{year}")
    public ResponseEntity<Map<String, Object>> getYearlyReport(@PathVariable int year) {
        User currentUser = authService.getCurrentUser();
        Map<String, Object> report = reportService.getYearlyReport(year, currentUser);
        return ResponseEntity.ok(report);
    }
}
