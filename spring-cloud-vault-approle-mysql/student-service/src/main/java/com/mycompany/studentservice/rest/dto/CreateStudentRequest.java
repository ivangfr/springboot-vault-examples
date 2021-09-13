package com.mycompany.studentservice.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateStudentRequest {

    @Schema(example = "Ivan")
    @NotBlank
    private String firstName;

    @Schema(example = "Franchin")
    @NotBlank
    private String lastName;

    @Schema(example = "ivan.franchin@test.com")
    @NotBlank
    private String email;
}
