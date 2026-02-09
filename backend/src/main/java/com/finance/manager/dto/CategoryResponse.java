package com.finance.manager.dto;

import com.finance.manager.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    
    private String name;
    private TransactionType type;
    private boolean isCustom;
}
