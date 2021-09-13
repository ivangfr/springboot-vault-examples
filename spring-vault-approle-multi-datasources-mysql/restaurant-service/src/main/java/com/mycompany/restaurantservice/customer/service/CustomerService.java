package com.mycompany.restaurantservice.customer.service;

import com.mycompany.restaurantservice.customer.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getCustomers();

    Customer saveCustomer(Customer customer);
}
