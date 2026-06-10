package com.projekat.biblioteka.service;

import com.projekat.biblioteka.model.Book;
import com.projekat.biblioteka.model.Loan;
import com.projekat.biblioteka.model.Member;
import com.projekat.biblioteka.repository.BookRepository;
import com.projekat.biblioteka.repository.LoanRepository;
import com.projekat.biblioteka.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoanService loanService;

    private Book availableBook;
    private Book unavailableBook;
    private Member activeMember;
    private Member inactiveMember;
    private Loan activeLoan;

    @BeforeEach
    void setUp() {
        availableBook = new Book();
        availableBook.setId(1L);
        availableBook.setTitle("Dune");
        availableBook.setAvailableCopies(2);

        unavailableBook = new Book();
        unavailableBook.setId(2L);
        unavailableBook.setTitle("1984");
        unavailableBook.setAvailableCopies(0);

        activeMember = new Member();
        activeMember.setId(1L);
        activeMember.setFirstName("Ana");
        activeMember.setLastName("Jović");
        activeMember.setActive(true);

        inactiveMember = new Member();
        inactiveMember.setId(2L);
        inactiveMember.setFirstName("Petar");
        inactiveMember.setLastName("Lazić");
        inactiveMember.setActive(false);

        activeLoan = new Loan();
        activeLoan.setId(1L);
        activeLoan.setBook(availableBook);
        activeLoan.setMember(activeMember);
        activeLoan.setLoanDate(LocalDate.now().minusDays(5));
        activeLoan.setDueDate(LocalDate.now().plusDays(9));
        activeLoan.setReturned(false);
    }

    @Test
    void testCreateLoan_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepository.save(any(Book.class))).thenReturn(availableBook);
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);

        Loan result = loanService.createLoan(1L, 1L, 14);

        assertNotNull(result);
        assertEquals(1, availableBook.getAvailableCopies());
        verify(bookRepository).save(availableBook);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void testCreateLoan_BookNotAvailable_ThrowsException() {
        when(bookRepository.findById(2L)).thenReturn(Optional.of(unavailableBook));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> loanService.createLoan(2L, 1L, 14));

        assertTrue(ex.getMessage().contains("nije dostupna"));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void testCreateLoan_BookNotFound_ThrowsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> loanService.createLoan(99L, 1L, 14));
    }

    @Test
    void testCreateLoan_InactiveMember_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(inactiveMember));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> loanService.createLoan(1L, 2L, 14));

        assertTrue(ex.getMessage().contains("nije aktivan"));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void testCreateLoan_InvalidDays_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));

        assertThrows(RuntimeException.class, () -> loanService.createLoan(1L, 1L, 0));
        assertThrows(RuntimeException.class, () -> loanService.createLoan(1L, 1L, 31));
    }

    @Test
    void testReturnBook_Success() {
        availableBook.setAvailableCopies(1);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(bookRepository.save(any(Book.class))).thenReturn(availableBook);
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);

        Loan result = loanService.returnBook(1L);

        assertTrue(activeLoan.isReturned());
        assertNotNull(activeLoan.getReturnDate());
        assertEquals(2, availableBook.getAvailableCopies());
        verify(loanRepository).save(activeLoan);
    }

    @Test
    void testReturnBook_AlreadyReturned_ThrowsException() {
        activeLoan.setReturned(true);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));

        assertThrows(RuntimeException.class, () -> loanService.returnBook(1L));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void testReturnBook_NotFound_ThrowsException() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> loanService.returnBook(99L));
    }

    @Test
    void testGetActiveLoans() {
        when(loanRepository.findByReturnedFalse()).thenReturn(List.of(activeLoan));
        List<Loan> result = loanService.getActiveLoans();
        assertEquals(1, result.size());
        assertFalse(result.get(0).isReturned());
    }

    @Test
    void testGetOverdueLoans() {
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(List.of(activeLoan));
        List<Loan> result = loanService.getOverdueLoans();
        assertEquals(1, result.size());
    }
}
