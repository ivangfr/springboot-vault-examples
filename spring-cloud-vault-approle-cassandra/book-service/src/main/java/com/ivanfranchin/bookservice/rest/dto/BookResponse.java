package com.ivanfranchin.bookservice.rest.dto;

import com.ivanfranchin.bookservice.model.Book;

public record BookResponse(String id, String title, String author) {

    public static BookResponse from(Book book) {
        return new BookResponse(book.getId().toString(), book.getTitle(), book.getAuthor());
    }
}
