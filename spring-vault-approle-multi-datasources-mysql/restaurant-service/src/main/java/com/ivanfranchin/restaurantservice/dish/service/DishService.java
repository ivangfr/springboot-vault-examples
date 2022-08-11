package com.ivanfranchin.restaurantservice.dish.service;

import com.ivanfranchin.restaurantservice.dish.model.Dish;

import java.util.List;

public interface DishService {

    List<Dish> getDishes();

    Dish saveDish(Dish dish);
}
