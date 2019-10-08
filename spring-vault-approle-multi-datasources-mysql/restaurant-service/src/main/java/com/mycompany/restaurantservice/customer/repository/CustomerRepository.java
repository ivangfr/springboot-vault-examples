package com.mycompany.restaurantservice.customer.repository;

import com.mycompany.restaurantservice.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
