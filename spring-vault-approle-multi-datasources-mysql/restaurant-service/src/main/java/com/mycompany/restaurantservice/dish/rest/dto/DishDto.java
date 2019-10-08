package com.mycompany.restaurantservice.dish.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishDto {

    private Long id;
    private String name;
    private BigDecimal price;

}
