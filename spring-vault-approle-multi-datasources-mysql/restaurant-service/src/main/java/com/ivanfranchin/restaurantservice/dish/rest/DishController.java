package com.ivanfranchin.restaurantservice.dish.rest;

import com.ivanfranchin.restaurantservice.dish.mapper.DishMapper;
import com.ivanfranchin.restaurantservice.dish.model.Dish;
import com.ivanfranchin.restaurantservice.dish.rest.dto.CreateDishRequest;
import com.ivanfranchin.restaurantservice.dish.rest.dto.DishResponse;
import com.ivanfranchin.restaurantservice.dish.service.DishService;
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
    public List<DishResponse> getDishes() {
        return dishService.getDishes()
                .stream()
                .map(dishMapper::toDishResponse)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DishResponse createDish(@Valid @RequestBody CreateDishRequest createDishRequest) {
        Dish dish = dishMapper.toDish(createDishRequest);
        dish = dishService.saveDish(dish);
        return dishMapper.toDishResponse(dish);
    }
}
