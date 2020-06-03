package com.mycompany.restaurantservice.customer.mapper;

import com.mycompany.restaurantservice.customer.model.Customer;
import com.mycompany.restaurantservice.customer.rest.dto.CreateCustomerDto;
import com.mycompany.restaurantservice.customer.rest.dto.CustomerDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toCustomer(CreateCustomerDto createCustomerDto);

    CustomerDto toCustomerDto(Customer customer);

}
