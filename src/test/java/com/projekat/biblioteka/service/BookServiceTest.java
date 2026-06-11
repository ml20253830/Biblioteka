package com.projekat.biblioteka.service;

import com.projekat.biblioteka.dto.OpenLibraryResponse;
import com.projekat.biblioteka.model.Book;
import com.projekat.biblioteka.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookService, "apiUrl", "https://openlibrary.org/isbn/");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setIsbn("9780008402785");
        testBook.setTitle("Fire and Blood");
        testBook.setAuthor("George R. R. Martin");
        testBook.setTotalCopies(3);
        testBook.setAvailableCopies(3);
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(testBook));
        List<Book> result = bookService.getAllBooks();
        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById_Found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        Optional<Book> result = bookService.getBookById(1L);
        assertTrue(result.isPresent());
        assertEquals("Fire and Blood", result.get().getTitle());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Book> result = bookService.getBookById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveBook_Success() {
        when(bookRepository.save(testBook)).thenReturn(testBook);
        Book result = bookService.saveBook(testBook);
        assertNotNull(result);
        assertEquals("Fire and Blood", result.getTitle());
        verify(bookRepository).save(testBook);
    }

    @Test
    void testSaveBook_EmptyTitle_ThrowsException() {
        testBook.setTitle("");
        assertThrows(RuntimeException.class, () -> bookService.saveBook(testBook));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void testSaveBook_NullCopies_ThrowsException() {
        testBook.setTotalCopies(null);
        assertThrows(RuntimeException.class, () -> bookService.saveBook(testBook));
    }

    @Test
    void testSaveBook_ZeroCopies_ThrowsException() {
        testBook.setTotalCopies(0);
        assertThrows(RuntimeException.class, () -> bookService.saveBook(testBook));
    }

    @Test
    void testDeleteBook_NotFound_ThrowsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> bookService.deleteBook(99L));
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void testFetchAndSaveFromApi_AlreadyExists_ReturnsFromDb() {
        when(bookRepository.findByIsbn("9780008402785")).thenReturn(Optional.of(testBook));
        Book result = bookService.fetchAndSaveFromApi("9780008402785", 2);
        assertEquals("Fire and Blood", result.getTitle());
        verify(restTemplate, never()).getForObject(anyString(), eq(OpenLibraryResponse.class));
    }

    @Test
    void testFetchAndSaveFromApi_NullResponse_ThrowsException() {
        when(bookRepository.findByIsbn("0000000000")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(OpenLibraryResponse.class))).thenReturn(null);
        assertThrows(RuntimeException.class, () -> bookService.fetchAndSaveFromApi("0000000000", 1));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void testFetchAndSaveFromApi_Success() {
        OpenLibraryResponse response = new OpenLibraryResponse();
        response.setTitle("TIME OF CONTEMPT, WITCHER 4");
        response.setNumberOfPages(412);

        when(bookRepository.findByIsbn("9781473231092")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(OpenLibraryResponse.class))).thenReturn(response);

        Book savedBook = new Book();
        savedBook.setTitle("TIME OF CONTEMPT, WITCHER 4");
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Book result = bookService.fetchAndSaveFromApi("9781473231092", 2);
        assertNotNull(result);
        assertEquals("TIME OF CONTEMPT, WITCHER 4", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testFetchAndSaveFromApi_BlankIsbn_ThrowsException() {
        assertThrows(RuntimeException.class, () -> bookService.fetchAndSaveFromApi("", 1));
    }
}
