package com.ivanfranchin.restaurantservice.dish;

import com.ivanfranchin.restaurantservice.dish.model.Dish;
import com.ivanfranchin.restaurantservice.dish.dto.CreateDishRequest;
import com.ivanfranchin.restaurantservice.dish.dto.DishResponse;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishRepository dishRepository;
    private final Environment environment;

    public DishController(DishRepository dishRepository, Environment environment) {
        this.dishRepository = dishRepository;
        this.environment = environment;
    }

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
        return dishRepository.findAll()
                .stream()
                .map(DishResponse::from)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DishResponse createDish(@Valid @RequestBody CreateDishRequest createDishRequest) {
        Dish dish = dishRepository.save(Dish.from(createDishRequest));
        return DishResponse.from(dish);
    }
}
