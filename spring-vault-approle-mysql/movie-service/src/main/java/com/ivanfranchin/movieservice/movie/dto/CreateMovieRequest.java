package com.ivanfranchin.movieservice.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateMovieRequest(@Schema(example = "Resident Evil") @NotBlank String title) {
}
