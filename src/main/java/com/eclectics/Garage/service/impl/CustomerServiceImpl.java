package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Customer;
import com.eclectics.Garage.repository.CustomerRepository;
import com.eclectics.Garage.service.CustomerService;
import org.hibernate.id.enhanced.CustomOptimizerDescriptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(Customer customer) {
            Optional<Customer> customerExists = customerRepository.findByUniqueId(customer.getUniqueId());
            if (customerExists.isPresent()){
                throw new RuntimeException("This customer exist.");
            }

            boolean uniqueCutomerExists;
            Integer uniqueCustomerId;

            do {
                Random random = new Random();
                uniqueCustomerId = random.nextInt(8888889) + 1111111;

                uniqueCutomerExists = customerRepository.findByUniqueId(uniqueCustomerId).isPresent();
                if (uniqueCutomerExists){
                    throw new RuntimeException("A customer with this id already exist");
                }

            }while (uniqueCutomerExists);

            customer.setUniqueId(uniqueCustomerId);

        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) {
        Optional<Customer> existingCustomerOptional = customerRepository.findById(id);

        if (existingCustomerOptional.isPresent()) {
            Customer existingCustomer = existingCustomerOptional.get();

            existingCustomer.setFullName(customer.getFullName());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
            existingCustomer.setPassword(customer.getPassword());
            return customerRepository.save(existingCustomer);
        }else {
            throw new ResourceAccessException("Customer does not exist");
        }
    }

    @Override
    public String deleteCustomer(Long id) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()){
            customerRepository.deleteById(id);
            return "Customer Deleted";
        }else {
            return "No Customer with that id";
        }
    }

    @Override
    public Optional<Customer> getCustomerByUniqueId(Integer uniqueId) {
        return customerRepository.findByUniqueId(uniqueId);
    }
}
