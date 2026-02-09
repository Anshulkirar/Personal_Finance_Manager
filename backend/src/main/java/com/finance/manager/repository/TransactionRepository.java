package com.finance.manager.repository;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.TransactionType;
import com.finance.manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserOrderByDateDesc(User user);
    
    Optional<Transaction> findByIdAndUser(Long id, User user);
    
    List<Transaction> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
    
    List<Transaction> findByUserAndCategoryOrderByDateDesc(User user, String category);
    
    List<Transaction> findByUserAndTypeOrderByDateDesc(User user, TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:category IS NULL OR t.category = :category) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "ORDER BY t.date DESC")
    List<Transaction> findByFilters(@Param("user") User user,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("category") String category,
                                   @Param("type") TransactionType type);
    
    long countByUserAndCategory(User user, String category);
}
