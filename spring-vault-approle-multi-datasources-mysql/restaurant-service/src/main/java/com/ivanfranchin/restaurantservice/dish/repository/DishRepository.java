package com.ivanfranchin.restaurantservice.dish.repository;

import com.ivanfranchin.restaurantservice.dish.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
}
