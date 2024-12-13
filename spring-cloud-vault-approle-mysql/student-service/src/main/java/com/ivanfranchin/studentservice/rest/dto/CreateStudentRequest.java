package com.ivanfranchin.studentservice.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateStudentRequest(
        @Schema(example = "Ivan") @NotBlank String firstName,
        @Schema(example = "Franchin") @NotBlank String lastName,
        @Schema(example = "ivan.franchin@test.com") @NotBlank String email) {
}
