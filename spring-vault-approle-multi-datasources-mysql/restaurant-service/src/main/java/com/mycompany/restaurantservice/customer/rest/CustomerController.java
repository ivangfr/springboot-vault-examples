package com.mycompany.restaurantservice.customer.rest;

import com.mycompany.restaurantservice.customer.model.Customer;
import com.mycompany.restaurantservice.customer.rest.dto.CreateCustomerDto;
import com.mycompany.restaurantservice.customer.rest.dto.CustomerDto;
import com.mycompany.restaurantservice.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final Environment environment;
    private final MapperFacade mapperFacade;

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        return String.format("%s/%s",
                environment.getProperty("datasource.customer.username"),
                environment.getProperty("datasource.customer.password"));
    }

    @GetMapping("/secretMessage")
    public String getSecretMessage() {
        return environment.getProperty("secret.restaurant-service.message");
    }

    @GetMapping
    public List<CustomerDto> getCustomers() {
        return customerService.getCustomers()
                .stream()
                .map(customer -> mapperFacade.map(customer, CustomerDto.class))
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerDto createCustomer(@Valid @RequestBody CreateCustomerDto createCustomerDto) {
        Customer customer = mapperFacade.map(createCustomerDto, Customer.class);
        customer = customerService.saveCustomer(customer);
        return mapperFacade.map(customer, CustomerDto.class);
    }

}
