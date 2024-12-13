package com.ivanfranchin.restaurantservice.dish.mapper;

import com.ivanfranchin.restaurantservice.dish.model.Dish;
import com.ivanfranchin.restaurantservice.dish.rest.dto.CreateDishRequest;
import com.ivanfranchin.restaurantservice.dish.rest.dto.DishResponse;
import org.springframework.stereotype.Service;

@Service
public class DishMapperImpl implements DishMapper {

    @Override
    public Dish toDish(CreateDishRequest createDishRequest) {
        if (createDishRequest == null) {
            return null;
        }
        Dish dish = new Dish();
        dish.setName(createDishRequest.name());
        dish.setPrice(createDishRequest.price());
        return dish;
    }

    @Override
    public DishResponse toDishResponse(Dish dish) {
        if (dish == null) {
            return null;
        }
        return new DishResponse(dish.getId(), dish.getName(), dish.getPrice());
    }
}
