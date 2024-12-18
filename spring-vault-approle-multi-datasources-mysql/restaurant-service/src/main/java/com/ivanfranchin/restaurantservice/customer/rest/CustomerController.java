package com.ivanfranchin.restaurantservice.customer.rest;

import com.ivanfranchin.restaurantservice.customer.model.Customer;
import com.ivanfranchin.restaurantservice.customer.repository.CustomerRepository;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CreateCustomerRequest;
import com.ivanfranchin.restaurantservice.customer.rest.dto.CustomerResponse;
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

    private final CustomerRepository customerRepository;
    private final Environment environment;

    public CustomerController(CustomerRepository customerRepository, Environment environment) {
        this.customerRepository = customerRepository;
        this.environment = environment;
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
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest createCustomerRequest) {
        Customer customer = customerRepository.save(Customer.from(createCustomerRequest));
        return CustomerResponse.from(customer);
    }
}
