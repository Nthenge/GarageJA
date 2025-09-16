package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.AutoMobiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomobilesRepository extends JpaRepository<AutoMobiles, Long> {
}
