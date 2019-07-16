package com.mycompany.studentservice.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateStudentDto {

    @ApiModelProperty(example = "Ivan")
    @NotBlank
    private String firstName;

    @ApiModelProperty(position = 2, example = "Franchin")
    @NotBlank
    private String lastName;

    @ApiModelProperty(position = 3, example = "ivan.franchin@test.com")
    @NotBlank
    private String email;

}
