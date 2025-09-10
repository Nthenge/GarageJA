package com.eclectics.Garage.service;


import com.eclectics.Garage.model.Customer;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Optional<Customer> getCustomerById(Long id);
    Optional<Customer> getCustomerByEmail(String email);
    List<Customer> getAllCustomers();
    Customer updateCustomer(Long id, Customer customer);
    String deleteCustomer(Long id);
    Optional<Customer> getCustomerByUniqueId(Integer uniqueId);
}

