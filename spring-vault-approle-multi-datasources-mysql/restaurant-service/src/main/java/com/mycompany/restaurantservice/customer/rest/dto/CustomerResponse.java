package com.mycompany.restaurantservice.customer.rest.dto;

import lombok.Value;

@Value
public class CustomerResponse {

    Long id;
    String name;
    String email;
}
