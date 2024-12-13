package com.ivanfranchin.restaurantservice.dish.service;

import com.ivanfranchin.restaurantservice.dish.model.Dish;
import com.ivanfranchin.restaurantservice.dish.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    public DishServiceImpl(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public List<Dish> getDishes() {
        return dishRepository.findAll();
    }

    @Override
    public Dish saveDish(Dish dish) {
        return dishRepository.save(dish);
    }
}
