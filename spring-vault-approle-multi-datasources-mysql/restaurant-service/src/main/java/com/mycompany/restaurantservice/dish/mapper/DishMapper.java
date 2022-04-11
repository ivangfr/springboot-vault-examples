package com.mycompany.restaurantservice.dish.mapper;

import com.mycompany.restaurantservice.dish.model.Dish;
import com.mycompany.restaurantservice.dish.rest.dto.CreateDishRequest;
import com.mycompany.restaurantservice.dish.rest.dto.DishResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DishMapper {

    @Mapping(target = "id", ignore = true)
    Dish toDish(CreateDishRequest createDishRequest);

    DishResponse toDishResponse(Dish dish);
}
