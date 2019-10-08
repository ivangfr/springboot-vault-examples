package com.mycompany.restaurantservice.dish.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateDishDto {

    @ApiModelProperty(example = "Pizza 4 Cheese")
    @NotBlank
    private String name;

    @ApiModelProperty(position = 1, example = "5.90")
    @NotNull
    private BigDecimal price;

}
