package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.service.GarageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GarageServiceImpl implements GarageService {

    public GarageServiceImpl(GarageRepository garageRepository) {
        this.garageRepository = garageRepository;
    }

    private final GarageRepository garageRepository;

    @Override
    public Garage createGarage(Garage garage) {

        Optional<Garage> GarageExists = garageRepository.findByName(garage.getName());
        if (GarageExists.isPresent()){
            throw new RuntimeException("Garage with this name exists");
        }

        boolean uniqueAdminIdExists;
        long uniqueAdminId;

        do {
            Random random = new Random();
            uniqueAdminId = random.nextInt(90000)+ 10000;

            uniqueAdminIdExists = garageRepository.findByGarageId(uniqueAdminId).isPresent();
            if (uniqueAdminIdExists){
                throw new RuntimeException("A garage with this Admin id already exist");
            }

        }while (uniqueAdminIdExists);

        garage.setGarageId(uniqueAdminId);

        return garageRepository.save(garage);
    }

    @Override
    public Optional<Garage> getGarageById(Long garageId) {
        return garageRepository.findByGarageId(garageId);
    }

    @Override
    public Optional<Garage> getGarageByName(String name) {
        return garageRepository.findByName(name);
    }


    @Override
    public List<Garage> getAllGarages() {
        return garageRepository.findAll();
    }

    public Garage updateGarage(Long id, Garage garage) {
        return garageRepository.findById(id).map(existingGarage -> {
            if (garage.getName() != null) existingGarage.setName(garage.getName());
            if (garage.getLocation() != null) existingGarage.setLocation(garage.getLocation());
            if (garage.getPhone() != null) existingGarage.setPhone(garage.getPhone());
            if (garage.getGarageId() != null)existingGarage.setGarageId(garage.getGarageId());
            return garageRepository.save(existingGarage);
        }).orElseThrow(() -> new RuntimeException("Garage with id " + id + " does not exist"));
    }

    @Override
    public void deleteGarage(Long id) {
        if (!garageRepository.existsById(id)){
            throw new RuntimeException("Garage with id " + id + " does not exist");
        }
        garageRepository.deleteById(id);
    }
}
