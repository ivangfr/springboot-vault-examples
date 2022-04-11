package com.mycompany.restaurantservice.customer.mapper;

import com.mycompany.restaurantservice.customer.model.Customer;
import com.mycompany.restaurantservice.customer.rest.dto.CreateCustomerRequest;
import com.mycompany.restaurantservice.customer.rest.dto.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer toCustomer(CreateCustomerRequest createCustomerRequest);

    CustomerResponse toCustomerResponse(Customer customer);
}
