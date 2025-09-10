package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Customer;
import org.hibernate.id.enhanced.CustomOptimizerDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find by email (useful for login or unique validation)
    Optional<Customer> findByEmail(String email);

    // Check if email already exists
    boolean existsByEmail(String email);

    // Find by phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByUniqueId(Integer uniqueId);
}


