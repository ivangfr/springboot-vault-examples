package com.ivanfranchin.restaurantservice.customer.repository;

import com.ivanfranchin.restaurantservice.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
