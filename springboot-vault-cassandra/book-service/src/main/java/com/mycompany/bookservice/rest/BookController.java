package com.mycompany.bookservice.rest;

import com.mycompany.bookservice.model.Book;
import com.mycompany.bookservice.rest.dto.BookDto;
import com.mycompany.bookservice.rest.dto.CreateBookDto;
import com.mycompany.bookservice.service.BookService;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private final BookService bookService;
    private final MapperFacade mapperFacade;

    public BookController(BookService bookService, MapperFacade mapperFacade) {
        this.bookService = bookService;
        this.mapperFacade = mapperFacade;
    }

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        log.info("Get database credentials");

        return String.format("%s/%s", username, password);
    }

    @GetMapping
    public List<BookDto> getBooks() {
        log.info("Get books");

        return bookService.getBooks()
                .stream()
                .map(book -> mapperFacade.map(book, BookDto.class))
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookDto createBook(@Valid @RequestBody CreateBookDto createBookDto) {
        log.info("Post request to create book: {}", createBookDto);

        Book book = mapperFacade.map(createBookDto, Book.class);
        book = bookService.saveBook(book);

        return mapperFacade.map(book, BookDto.class);
    }

}
