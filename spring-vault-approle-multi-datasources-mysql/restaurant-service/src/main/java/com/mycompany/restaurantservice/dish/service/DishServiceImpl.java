package com.mycompany.restaurantservice.dish.service;

import com.mycompany.restaurantservice.dish.model.Dish;
import com.mycompany.restaurantservice.dish.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    @Override
    public List<Dish> getDishes() {
        return dishRepository.findAll();
    }

    @Override
    public Dish saveDish(Dish dish) {
        return dishRepository.save(dish);
    }

}
