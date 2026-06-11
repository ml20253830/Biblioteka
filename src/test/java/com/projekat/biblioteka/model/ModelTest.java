package com.projekat.biblioteka.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testBookGettersSetters() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Fire and Blood");
        book.setAuthor("George R. R. Martin");
        book.setIsbn("9780008402785");
        book.setAvailableCopies(3);
        book.setTotalCopies(5);

        assertEquals(1L, book.getId());
        assertEquals("Fire and Blood", book.getTitle());
        assertEquals("George R. R. Martin", book.getAuthor());
        assertEquals(3, book.getAvailableCopies());
        assertEquals(5, book.getTotalCopies());
    }

    @Test
    void testMemberGettersSetters() {
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Mateja");
        member.setLastName("Lazic");
        member.setEmail("mateja@test.com");
        member.setActive(true);
        member.setMembershipDate(LocalDate.of(2026, 6, 11));

        assertEquals("Mateja", member.getFirstName());
        assertEquals("Lazic", member.getLastName());
        assertTrue(member.isActive());
        assertEquals(LocalDate.of(2026, 6, 11), member.getMembershipDate());
    }

    @Test
    void testLoanGettersSetters() {
        Book book = new Book();
        book.setTitle("TIME OF CONTEMPT, WITCHER 4");

        Member member = new Member();
        member.setFirstName("Lazar");

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setReturned(false);

        assertEquals("TIME OF CONTEMPT, WITCHER 4", loan.getBook().getTitle());
        assertEquals("Lazar", loan.getMember().getFirstName());
        assertFalse(loan.isReturned());
        assertNull(loan.getReturnDate());
    }
}
