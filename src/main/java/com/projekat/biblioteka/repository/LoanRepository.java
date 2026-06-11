package com.projekat.biblioteka.repository;

import com.projekat.biblioteka.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberId(Long memberId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByReturnedFalse();

    @Query("SELECT l FROM Loan l WHERE l.returned = false AND l.dueDate < :today")
    List<Loan> findOverdueLoans(LocalDate today);
}
