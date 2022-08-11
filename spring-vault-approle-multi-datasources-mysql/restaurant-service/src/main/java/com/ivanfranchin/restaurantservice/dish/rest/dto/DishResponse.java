package com.ivanfranchin.restaurantservice.dish.rest.dto;

import java.math.BigDecimal;

public record DishResponse(Long id, String name, BigDecimal price) {
}
