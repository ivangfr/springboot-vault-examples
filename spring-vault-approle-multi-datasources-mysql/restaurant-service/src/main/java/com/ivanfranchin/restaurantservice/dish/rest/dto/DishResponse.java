package com.ivanfranchin.restaurantservice.dish.rest.dto;

import com.ivanfranchin.restaurantservice.dish.model.Dish;

import java.math.BigDecimal;

public record DishResponse(Long id, String name, BigDecimal price) {

    public static DishResponse from(Dish dish) {
        return new DishResponse(dish.getId(), dish.getName(), dish.getPrice());
    }
}
