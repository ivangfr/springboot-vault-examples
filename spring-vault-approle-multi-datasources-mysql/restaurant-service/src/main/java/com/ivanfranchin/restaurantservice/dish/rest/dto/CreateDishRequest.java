package com.ivanfranchin.restaurantservice.dish.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDishRequest {

    @Schema(example = "Pizza 4 Cheese")
    @NotBlank
    private String name;

    @Schema(example = "5.90")
    @NotNull
    private BigDecimal price;
}
