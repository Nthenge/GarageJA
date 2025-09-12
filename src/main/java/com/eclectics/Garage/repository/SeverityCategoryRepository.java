package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.SeverityCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeverityCategoryRepository extends JpaRepository<SeverityCategories, Long> {
    Optional<SeverityCategories> findBySeverityName(String severityName);
}
