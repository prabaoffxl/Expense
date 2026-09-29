package com.expensejar.repository;

import com.expensejar.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Query("SELECT b FROM Budget b JOIN FETCH b.user JOIN FETCH b.category WHERE b.user.id = :userId AND b.category.id = :categoryId")
    Optional<Budget> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT b FROM Budget b JOIN FETCH b.user JOIN FETCH b.category WHERE b.user.id = :userId")
    List<Budget> findByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);
}
