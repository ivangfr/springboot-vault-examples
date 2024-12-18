package com.ivanfranchin.restaurantservice.customer.rest.dto;

import com.ivanfranchin.restaurantservice.customer.model.Customer;

public record CustomerResponse(Long id, String name, String email) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail());
    }
}
