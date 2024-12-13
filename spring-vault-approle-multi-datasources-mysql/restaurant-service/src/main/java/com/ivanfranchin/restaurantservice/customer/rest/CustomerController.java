package com.ivanfranchin.restaurantservice.customer.rest;

import com.ivanfranchin.restaurantservice.customer.mapper.CustomerMapper;
import com.ivanfranchin.restaurantservice.customer.model.Customer;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CreateCustomerRequest;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CustomerResponse;
import com.ivanfranchin.restaurantservice.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final Environment environment;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, Environment environment, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.environment = environment;
        this.customerMapper = customerMapper;
    }

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
    public List<CustomerResponse> getCustomers() {
        return customerService.getCustomers()
                .stream()
                .map(customerMapper::toCustomerResponse)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest createCustomerRequest) {
        Customer customer = customerMapper.toCustomer(createCustomerRequest);
        customer = customerService.saveCustomer(customer);
        return customerMapper.toCustomerResponse(customer);
    }
}
