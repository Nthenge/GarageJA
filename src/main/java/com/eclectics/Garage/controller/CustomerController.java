package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.Customer;
import com.eclectics.Garage.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

        CustomerService customerService;

        public CustomerController(CustomerService customerService) {
            this.customerService = customerService;
        }

        @GetMapping("/{customerId}")
        public Optional<Customer> getOneCustomer(@PathVariable("customerId") Long Id){
            return customerService.getCustomerById(Id);
        }
        @GetMapping("/search/{customerUniqueId}")
        public Optional<Customer> getCustomerByUniqueId(@PathVariable("customerUniqueId") Integer customerUniqueId){
            return customerService.getCustomerByUniqueId(customerUniqueId);
        }

        @GetMapping()
        public List<Customer> getAllCustomers(){
            return customerService.getAllCustomers();
        }

        @PostMapping()
        public Customer createCustomer(@RequestBody Customer customer){
            return customerService.createCustomer(customer);
        }

        @PutMapping("/{customerId}")
        public String updateCustomer(@PathVariable Long customerId, @RequestBody Customer customer){
            customerService.updateCustomer(customerId, customer);
            return "Customer updated successfully";
        }

        @DeleteMapping("/{customerId}")
        public String deleteACustomer(@PathVariable("customerId") Long customerId){
            customerService.deleteCustomer(customerId);
            return "Customer Deleted Succesfully";
        }

    }
