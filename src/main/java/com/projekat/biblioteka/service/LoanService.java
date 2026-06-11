package com.projekat.biblioteka.service;

import com.projekat.biblioteka.model.Book;
import com.projekat.biblioteka.model.Loan;
import com.projekat.biblioteka.model.Member;
import com.projekat.biblioteka.repository.BookRepository;
import com.projekat.biblioteka.repository.LoanRepository;
import com.projekat.biblioteka.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findByReturnedFalse();
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now());
    }

    public List<Loan> getLoansByMember(Long memberId) {
        return loanRepository.findByMemberId(memberId);
    }

    public Loan createLoan(Long bookId, Long memberId, int loanDays) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Knjiga '" + book.getTitle() + "' trenutno nije dostupna");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Član nije pronađen"));

        if (!member.isActive()) {
            throw new RuntimeException("Član '" + member.getFirstName() + " " + member.getLastName() + "' nije aktivan");
        }

        if (loanDays < 1 || loanDays > 30) {
            throw new RuntimeException("Period pozajmice mora biti između 1 i 30 dana");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(loanDays));
        loan.setReturned(false);

        return loanRepository.save(loan);
    }

    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Pozajmica sa ID " + loanId + " nije pronađena"));

        if (loan.isReturned()) {
            throw new RuntimeException("Knjiga je već vraćena");
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }
}
