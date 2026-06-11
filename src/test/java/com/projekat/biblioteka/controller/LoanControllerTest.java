package com.projekat.biblioteka.controller;

import com.projekat.biblioteka.model.Book;
import com.projekat.biblioteka.model.Loan;
import com.projekat.biblioteka.model.Member;
import com.projekat.biblioteka.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    private Loan testLoan;

    @BeforeEach
    void setUp() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Fire and Blood");

        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Mateja");

        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setBook(book);
        testLoan.setMember(member);
        testLoan.setLoanDate(LocalDate.now());
        testLoan.setDueDate(LocalDate.now().plusDays(14));
        testLoan.setReturned(false);
    }

    @Test
    void testGetAllLoans_ReturnsOk() throws Exception {
        when(loanService.getAllLoans()).thenReturn(List.of(testLoan));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetActiveLoans_ReturnsOk() throws Exception {
        when(loanService.getActiveLoans()).thenReturn(List.of(testLoan));

        mockMvc.perform(get("/api/loans/active"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetOverdueLoans_ReturnsOk() throws Exception {
        when(loanService.getOverdueLoans()).thenReturn(List.of());

        mockMvc.perform(get("/api/loans/overdue"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateLoan_ReturnsOk() throws Exception {
        when(loanService.createLoan(anyLong(), anyLong(), anyInt())).thenReturn(testLoan);

        mockMvc.perform(post("/api/loans")
                        .param("bookId", "1")
                        .param("memberId", "1")
                        .param("loanDays", "14"))
                .andExpect(status().isOk());
    }

    @Test
    void testReturnBook_ReturnsOk() throws Exception {
        testLoan.setReturned(true);
        when(loanService.returnBook(1L)).thenReturn(testLoan);

        mockMvc.perform(patch("/api/loans/1/return"))
                .andExpect(status().isOk());
    }
}
