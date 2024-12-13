package com.ivanfranchin.restaurantservice.customer.mapper;

import com.ivanfranchin.restaurantservice.customer.model.Customer;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CreateCustomerRequest;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CustomerResponse;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toCustomer(CreateCustomerRequest createCustomerRequest) {
        if (createCustomerRequest == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setName(createCustomerRequest.name());
        customer.setEmail(createCustomerRequest.email());
        return customer;
    }

    @Override
    public CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail());
    }
}
