package com.mycompany.restaurantservice.dish.rest;

import com.mycompany.restaurantservice.dish.mapper.DishMapper;
import com.mycompany.restaurantservice.dish.model.Dish;
import com.mycompany.restaurantservice.dish.rest.dto.CreateDishDto;
import com.mycompany.restaurantservice.dish.rest.dto.DishDto;
import com.mycompany.restaurantservice.dish.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;
    private final Environment environment;
    private final DishMapper dishMapper;

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        return String.format("%s/%s",
                environment.getProperty("datasource.dish.username"),
                environment.getProperty("datasource.dish.password"));
    }

    @GetMapping("/secretMessage")
    public String getSecretMessage() {
        return environment.getProperty("secret.restaurant-service.message");
    }

    @GetMapping
    public List<DishDto> getDishes() {
        return dishService.getDishes()
                .stream()
                .map(dishMapper::toDishDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DishDto createDish(@Valid @RequestBody CreateDishDto createDishDto) {
        Dish dish = dishMapper.toDish(createDishDto);
        dish = dishService.saveDish(dish);
        return dishMapper.toDishDto(dish);
    }

}
