package com.mycompany.bookservice.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateBookRequest {

    @Schema(example = "Spring Boot in a Nutshell")
    private String title;

    @Schema(example = "Ivan Franchin")
    private String author;
}
