package com.eclectics.Garage.service;
import com.eclectics.Garage.model.Garage;

import java.util.List;
import java.util.Optional;

public interface GarageService {
    Garage createGarage(Garage garage);
    Optional<Garage> getGarageById(Long garageId);
    Optional<Garage> getGarageByName(String name);
    List<Garage> getAllGarages();
    Garage updateGarage(Long id, Garage garage);
    void deleteGarage(Long id);
}

