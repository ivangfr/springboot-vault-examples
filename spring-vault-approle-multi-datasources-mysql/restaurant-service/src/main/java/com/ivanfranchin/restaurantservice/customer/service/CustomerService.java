package com.ivanfranchin.restaurantservice.customer.service;

import com.ivanfranchin.restaurantservice.customer.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getCustomers();

    Customer saveCustomer(Customer customer);
}
