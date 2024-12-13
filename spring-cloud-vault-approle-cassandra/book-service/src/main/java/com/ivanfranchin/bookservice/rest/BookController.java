package com.ivanfranchin.bookservice.rest;

import com.ivanfranchin.bookservice.model.Book;
import com.ivanfranchin.bookservice.repository.BookRepository;
import com.ivanfranchin.bookservice.rest.dto.BookResponse;
import com.ivanfranchin.bookservice.rest.dto.CreateBookRequest;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final Environment environment;

    public BookController(BookRepository bookRepository, Environment environment) {
        this.bookRepository = bookRepository;
        this.environment = environment;
    }

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        return String.format("%s/%s",
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("spring.datasource.password"));
    }

    @GetMapping("/secretMessage")
    public String getSecretMessage() {
        return environment.getProperty("message");
    }

    @GetMapping
    public List<BookResponse> getBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toBookResponse)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest createBookRequest) {
        Book book = toBook(createBookRequest);
        book = bookRepository.save(book);
        return toBookResponse(book);
    }

    public Book toBook(CreateBookRequest createBookRequest) {
        return new Book(UUID.randomUUID(), createBookRequest.title(), createBookRequest.author());
    }

    public BookResponse toBookResponse(Book book) {
        return new BookResponse(book.getId().toString(), book.getTitle(), book.getAuthor());
    }
}
