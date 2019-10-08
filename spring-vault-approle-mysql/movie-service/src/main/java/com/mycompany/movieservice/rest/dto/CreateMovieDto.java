package com.mycompany.movieservice.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateMovieDto {

    @ApiModelProperty(example = "Resident Evil")
    @NotBlank
    private String title;

}
