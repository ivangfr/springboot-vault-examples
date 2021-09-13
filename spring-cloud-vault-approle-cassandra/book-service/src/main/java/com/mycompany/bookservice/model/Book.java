package com.mycompany.bookservice.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@Table("books")
public class Book {

    @PrimaryKey
    private UUID id;
    private String title;
    private String author;
}
