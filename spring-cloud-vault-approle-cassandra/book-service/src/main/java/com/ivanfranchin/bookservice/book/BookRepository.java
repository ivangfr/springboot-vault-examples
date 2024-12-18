package com.ivanfranchin.bookservice.book;

import com.ivanfranchin.bookservice.book.model.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends CassandraRepository<Book, UUID> {
}
