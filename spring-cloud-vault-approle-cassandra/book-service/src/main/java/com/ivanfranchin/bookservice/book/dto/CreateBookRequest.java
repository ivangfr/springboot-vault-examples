package com.ivanfranchin.bookservice.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
        @Schema(example = "Spring Boot in a Nutshell") @NotBlank String title,
        @Schema(example = "Ivan Franchin") @NotBlank String author) {
}
