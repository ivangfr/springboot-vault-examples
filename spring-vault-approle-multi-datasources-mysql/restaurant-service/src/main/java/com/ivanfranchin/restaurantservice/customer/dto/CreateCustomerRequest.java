package com.ivanfranchin.restaurantservice.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
        @Schema(example = "Ivan Franchin") @NotBlank String name,
        @Schema(example = "ivan.franchin@test.com") @Email String email) {
}
