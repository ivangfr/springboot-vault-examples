package com.mycompany.restaurantservice.dish.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateDishDto {

    @Schema(example = "Pizza 4 Cheese")
    @NotBlank
    private String name;

    @Schema(example = "5.90")
    @NotNull
    private BigDecimal price;

}
