package com.mycompany.restaurantservice.dish.mapper;

import com.mycompany.restaurantservice.dish.model.Dish;
import com.mycompany.restaurantservice.dish.rest.dto.CreateDishDto;
import com.mycompany.restaurantservice.dish.rest.dto.DishDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DishMapper {

    Dish toDish(CreateDishDto createDishDto);

    DishDto toDishDto(Dish dish);

}
