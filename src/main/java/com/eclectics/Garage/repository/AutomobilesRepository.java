package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.AutoMobiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutomobilesRepository extends JpaRepository<AutoMobiles, Long> {
    @Query("SELECT a.make FROM AutoMobiles a")
    List<String> findAllMakes();

    @Query("SELECT a.year FROM AutoMobiles a")
    List<String> findAllYears();

    @Query("SELECT a.engineType FROM AutoMobiles a")
    List<String> findAllEngineType();

    @Query("SELECT a.transmission FROM AutoMobiles a")
    List<String> findAllTransmission();

}
