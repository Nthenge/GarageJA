package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.ServiceCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategories, Long> {
    Optional<ServiceCategories> serviceCategoryName(String serviceCategoryName);
}
