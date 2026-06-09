package com.projekat.biblioteka.service;

import com.projekat.biblioteka.repository.BookRepository;
import com.projekat.biblioteka.repository.LoanRepository;
import com.projekat.biblioteka.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public StatisticsService(BookRepository bookRepository,
                             MemberRepository memberRepository,
                             LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalBooks", bookRepository.count());
        stats.put("totalMembers", memberRepository.count());
        stats.put("activeLoans", (long) loanRepository.findByReturnedFalse().size());
        stats.put("overdueLoans", (long) loanRepository.findOverdueLoans(LocalDate.now()).size());
        return stats;
    }
}
