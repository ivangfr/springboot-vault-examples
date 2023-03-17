package com.ivanfranchin.restaurantservice.dish.mapper;

import com.ivanfranchin.restaurantservice.dish.model.Dish;
import com.ivanfranchin.restaurantservice.dish.rest.dto.CreateDishRequest;
import com.ivanfranchin.restaurantservice.dish.rest.dto.DishResponse;

public interface DishMapper {

    Dish toDish(CreateDishRequest createDishRequest);

    DishResponse toDishResponse(Dish dish);
}
