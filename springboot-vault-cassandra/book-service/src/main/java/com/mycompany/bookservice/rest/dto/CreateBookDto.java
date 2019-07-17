package com.mycompany.bookservice.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateBookDto {

    @ApiModelProperty(example = "Spring-boot in a Nutshell")
    private String title;

    @ApiModelProperty(position = 2, example = "Ivan Franchin")
    private String author;

}
