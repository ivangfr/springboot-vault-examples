package com.ivanfranchin.movieservice.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateMovieRequest {

    @Schema(example = "Resident Evil")
    @NotBlank
    private String title;
}
