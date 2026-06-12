package com.projekat.biblioteka.service;

import com.projekat.biblioteka.model.Loan;
import com.projekat.biblioteka.repository.BookRepository;
import com.projekat.biblioteka.repository.LoanRepository;
import com.projekat.biblioteka.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void testGetStats_ReturnsCorrectCounts() {
        when(bookRepository.count()).thenReturn(10L);
        when(memberRepository.count()).thenReturn(5L);
        when(loanRepository.findByReturnedFalse()).thenReturn(List.of(new Loan(), new Loan()));
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(List.of(new Loan()));

        Map<String, Long> stats = statisticsService.getStats();

        assertEquals(10L, stats.get("totalBooks"));
        assertEquals(5L, stats.get("totalMembers"));
        assertEquals(2L, stats.get("activeLoans"));
        assertEquals(1L, stats.get("overdueLoans"));
    }

    @Test
    void testGetStats_EmptyDatabase() {
        when(bookRepository.count()).thenReturn(0L);
        when(memberRepository.count()).thenReturn(0L);
        when(loanRepository.findByReturnedFalse()).thenReturn(List.of());
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(List.of());

        Map<String, Long> stats = statisticsService.getStats();

        assertEquals(0L, stats.get("totalBooks"));
        assertEquals(0L, stats.get("totalMembers"));
        assertEquals(0L, stats.get("activeLoans"));
        assertEquals(0L, stats.get("overdueLoans"));
    }

    @Test
    void testGetStats_ContainsAllKeys() {
        when(bookRepository.count()).thenReturn(3L);
        when(memberRepository.count()).thenReturn(2L);
        when(loanRepository.findByReturnedFalse()).thenReturn(List.of(new Loan()));
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(List.of());

        Map<String, Long> stats = statisticsService.getStats();

        assertTrue(stats.containsKey("totalBooks"));
        assertTrue(stats.containsKey("totalMembers"));
        assertTrue(stats.containsKey("activeLoans"));
        assertTrue(stats.containsKey("overdueLoans"));
        assertEquals(4, stats.size());
    }
}