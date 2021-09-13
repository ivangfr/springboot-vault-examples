package com.mycompany.restaurantservice.customer.mapper;

import com.mycompany.restaurantservice.customer.model.Customer;
import com.mycompany.restaurantservice.customer.rest.dto.CreateCustomerRequest;
import com.mycompany.restaurantservice.customer.rest.dto.CustomerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toCustomer(CreateCustomerRequest createCustomerRequest);

    CustomerResponse toCustomerResponse(Customer customer);
}
