package com.mycompany.movieservice.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateMovieDto {

    @ApiModelProperty(example = "Resident Evil")
    private String title;

}
