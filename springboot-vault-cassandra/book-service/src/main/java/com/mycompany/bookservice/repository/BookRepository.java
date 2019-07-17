package com.mycompany.bookservice.repository;

import com.mycompany.bookservice.model.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface BookRepository extends CassandraRepository<Book, String> {
}
