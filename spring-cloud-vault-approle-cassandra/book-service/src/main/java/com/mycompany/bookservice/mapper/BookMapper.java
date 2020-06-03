package com.mycompany.bookservice.mapper;

import com.mycompany.bookservice.model.Book;
import com.mycompany.bookservice.rest.dto.BookDto;
import com.mycompany.bookservice.rest.dto.CreateBookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UUIDMapper.class)
public interface BookMapper {

    Book toBook(CreateBookDto createBookDto);

    @Mapping(target = "id")
    BookDto toBookDto(Book book);

}
