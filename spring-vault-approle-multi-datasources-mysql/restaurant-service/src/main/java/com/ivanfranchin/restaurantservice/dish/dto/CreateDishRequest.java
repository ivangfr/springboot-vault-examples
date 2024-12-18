package com.ivanfranchin.restaurantservice.dish.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateDishRequest(
        @Schema(example = "Pizza 4 Cheese") @NotBlank String name,
        @Schema(example = "5.90") @NotNull BigDecimal price) {
}
