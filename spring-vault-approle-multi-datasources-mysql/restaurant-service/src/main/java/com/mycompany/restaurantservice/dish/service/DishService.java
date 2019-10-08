package com.mycompany.restaurantservice.dish.service;

import com.mycompany.restaurantservice.dish.model.Dish;

import java.util.List;

public interface DishService {

    List<Dish> getDishes();

    Dish saveDish(Dish dish);

}
