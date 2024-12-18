package com.ivanfranchin.bookservice.model;

import com.ivanfranchin.bookservice.rest.dto.CreateBookRequest;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("books")
public class Book {

    @PrimaryKey
    private UUID id;
    private String title;
    private String author;

    public Book() {
    }

    public Book(UUID id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public static Book from(CreateBookRequest createBookRequest) {
        return new Book(UUID.randomUUID(), createBookRequest.title(), createBookRequest.author());
    }
}
