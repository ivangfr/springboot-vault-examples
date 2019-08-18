package com.mycompany.bookservice.repository;

import com.mycompany.bookservice.model.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface BookRepository extends CassandraRepository<Book, UUID> {
}
