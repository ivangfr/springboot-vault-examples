package com.mycompany.bookservice.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateBookDto {

    @ApiModelProperty(example = "Spring Boot in a Nutshell")
    private String title;

    @ApiModelProperty(position = 1, example = "Ivan Franchin")
    private String author;

}
