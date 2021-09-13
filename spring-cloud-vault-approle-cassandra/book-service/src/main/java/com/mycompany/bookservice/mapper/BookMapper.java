package com.mycompany.bookservice.mapper;

import com.mycompany.bookservice.model.Book;
import com.mycompany.bookservice.rest.dto.BookResponse;
import com.mycompany.bookservice.rest.dto.CreateBookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UUIDMapper.class)
public interface BookMapper {

    Book toBook(CreateBookRequest createBookRequest);

    @Mapping(target = "id")
    BookResponse toBookResponse(Book book);
}
