package com.mycompany.restaurantservice.dish.rest.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class DishResponse {

    Long id;
    String name;
    BigDecimal price;
}
