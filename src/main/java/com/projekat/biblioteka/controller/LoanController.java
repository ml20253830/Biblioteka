package com.projekat.biblioteka.controller;

import com.projekat.biblioteka.model.Loan;
import com.projekat.biblioteka.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("/active")
    public List<Loan> getActiveLoans() {
        return loanService.getActiveLoans();
    }

    @GetMapping("/overdue")
    public List<Loan> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }

    @GetMapping("/member/{memberId}")
    public List<Loan> getLoansByMember(@PathVariable Long memberId) {
        return loanService.getLoansByMember(memberId);
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestParam Long bookId,
                                           @RequestParam Long memberId,
                                           @RequestParam(defaultValue = "14") int loanDays) {
        return ResponseEntity.ok(loanService.createLoan(bookId, memberId, loanDays));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }
}
