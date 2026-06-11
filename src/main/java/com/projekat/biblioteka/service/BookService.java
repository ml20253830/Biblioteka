package com.projekat.biblioteka.service;

import com.projekat.biblioteka.dto.OpenLibraryResponse;
import com.projekat.biblioteka.model.Book;
import com.projekat.biblioteka.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;

    @Value("${openlibrary.api.url}")
    private String apiUrl;

    public BookService(BookRepository bookRepository, RestTemplate restTemplate) {
        this.bookRepository = bookRepository;
        this.restTemplate = restTemplate;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new RuntimeException("Naslov knjige ne može biti prazan");
        }
        if (book.getTotalCopies() == null || book.getTotalCopies() < 1) {
            throw new RuntimeException("Broj primeraka mora biti najmanje 1");
        }
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Knjiga sa ID " + id + " nije pronađena"));
        bookRepository.deleteById(id);
    }

    public Book fetchAndSaveFromApi(String isbn, Integer totalCopies) {
        if (isbn == null || isbn.isBlank()) {
            throw new RuntimeException("ISBN ne može biti prazan");
        }

        Optional<Book> existing = bookRepository.findByIsbn(isbn);
        if (existing.isPresent()) {
            return existing.get();
        }

        String url = apiUrl + isbn + ".json";
        OpenLibraryResponse response = restTemplate.getForObject(url, OpenLibraryResponse.class);

        if (response == null || response.getTitle() == null) {
            throw new RuntimeException("Nije moguće preuzeti podatke za ISBN: " + isbn);
        }

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(response.getTitle());
        book.setAuthor(response.getFirstAuthor());
        book.setPageCount(response.getNumberOfPages());
        book.setTotalCopies(totalCopies != null ? totalCopies : 1);
        book.setAvailableCopies(book.getTotalCopies());

        return bookRepository.save(book);
    }
}
