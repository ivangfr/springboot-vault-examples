package com.ivanfranchin.restaurantservice.customer.mapper;

import com.ivanfranchin.restaurantservice.customer.model.Customer;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CreateCustomerRequest;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CustomerResponse;

public interface CustomerMapper {

    Customer toCustomer(CreateCustomerRequest createCustomerRequest);

    CustomerResponse toCustomerResponse(Customer customer);
}
