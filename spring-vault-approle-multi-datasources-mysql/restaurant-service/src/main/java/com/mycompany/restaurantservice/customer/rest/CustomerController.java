package com.mycompany.restaurantservice.customer.rest;

import com.mycompany.restaurantservice.customer.mapper.CustomerMapper;
import com.mycompany.restaurantservice.customer.model.Customer;
import com.mycompany.restaurantservice.customer.rest.dto.CreateCustomerRequest;
import com.mycompany.restaurantservice.customer.rest.dto.CustomerResponse;
import com.mycompany.restaurantservice.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
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
    private final CustomerMapper customerMapper;

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
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest createCustomerRequest) {
        Customer customer = customerMapper.toCustomer(createCustomerRequest);
        customer = customerService.saveCustomer(customer);
        return customerMapper.toCustomerResponse(customer);
    }
}
