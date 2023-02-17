package com.ivanfranchin.bookservice.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBookRequest {

    @Schema(example = "Spring Boot in a Nutshell")
    @NotBlank
    private String title;

    @Schema(example = "Ivan Franchin")
    @NotBlank
    private String author;
}
