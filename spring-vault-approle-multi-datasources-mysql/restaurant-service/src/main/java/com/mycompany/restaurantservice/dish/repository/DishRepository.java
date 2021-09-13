package com.mycompany.restaurantservice.dish.repository;

import com.mycompany.restaurantservice.dish.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
}
