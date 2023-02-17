package com.ivanfranchin.movieservice.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMovieRequest {

    @Schema(example = "Resident Evil")
    @NotBlank
    private String title;
}
