package com.ivanfranchin.bookservice.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateBookRequest {

    @Schema(example = "Spring Boot in a Nutshell")
    @NotBlank
    private String title;

    @Schema(example = "Ivan Franchin")
    @NotBlank
    private String author;
}
