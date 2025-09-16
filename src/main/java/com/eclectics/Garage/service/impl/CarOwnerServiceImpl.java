package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.AutoMobiles;
import com.eclectics.Garage.repository.AutomobilesRepository;
import com.eclectics.Garage.repository.CarOwnerRepository;
import com.eclectics.Garage.repository.SeverityCategoryRepository;
import com.eclectics.Garage.service.CarOwnerService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CarOwnerServiceImpl implements CarOwnerService {

    private final CarOwnerRepository carOwnerRepository;
    private final SeverityCategoryRepository severityCategoryRepository;
    private final AutomobilesRepository automobilesRepository;

    public CarOwnerServiceImpl(CarOwnerRepository carOwnerRepository, SeverityCategoryRepository severityCategoryRepository, AutomobilesRepository automobilesRepository) {
        this.carOwnerRepository = carOwnerRepository;
        this.severityCategoryRepository = severityCategoryRepository;
        this.automobilesRepository = automobilesRepository;
    }

    @Override
    public CarOwner createCarOwner(CarOwner carOwner) {
        Optional<CarOwner> carOwnerExists = carOwnerRepository.findByUniqueId(carOwner.getUniqueId());
        if (carOwnerExists.isPresent()) {
            throw new RuntimeException("This car owner exist.");
        }

        boolean uniqueCarOwnerExists;
        Integer uniqueCarOwnerId;

        do {
            Random random = new Random();
            uniqueCarOwnerId = random.nextInt(8888889) + 1111111;

            uniqueCarOwnerExists = carOwnerRepository.findByUniqueId(uniqueCarOwnerId).isPresent();
            if (uniqueCarOwnerExists) {
                throw new RuntimeException("A car owner with this id already exist");
            }

        } while (uniqueCarOwnerExists);

        carOwner.setUniqueId(uniqueCarOwnerId);

        if (carOwner.getAutomobile() != null) {
            AutoMobiles auto = automobilesRepository.findById(carOwner.getAutomobile().getId())
                    .orElseThrow(() -> new RuntimeException("Automobile with that ID does not exist"));
            carOwner.setAutomobile(auto);
        }

        return carOwnerRepository.save(carOwner);
    }
    @Override
    public Optional<CarOwner> findByUserId(Long userId) {
        return carOwnerRepository.findByUserId(userId);
    }

     @Override
    public boolean isDetailsCompleted(Long userId) {
        return carOwnerRepository.findByUserId(userId)
                .map(CarOwner::isComplete)
                .orElse(false);
    }


    @Override
    public CarOwner uploadDocument(Integer uniqueId, MultipartFile profilePic) throws java.io.IOException {
        CarOwner carOwner = carOwnerRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Car owner not found"));

        if (profilePic != null && !profilePic.isEmpty()) {
            carOwner.setProfilePic(profilePic.getBytes());
        }
        return carOwnerRepository.save(carOwner);
    }

    @Override
    public Optional<CarOwner> getCarOwnerById(Long id) {
        return carOwnerRepository.findById(id);
    }

    @Override
    public Optional<CarOwner> getCarOwnerByEmail(String licensePlate) {
        return carOwnerRepository.findByLicensePlate(licensePlate);
    }

    @Override
    public List<CarOwner> getAllCarOwners() {
        return carOwnerRepository.findAll();
    }

    @Override
    public CarOwner updateCarOwner(Long id, CarOwner carOwner) {
        Optional<CarOwner> existingCarOwnerOptional = carOwnerRepository.findById(id);

        if (existingCarOwnerOptional.isPresent()) {
            CarOwner eco = existingCarOwnerOptional.get();

            if (carOwner.getUniqueId() != null) eco.setUniqueId(carOwner.getUniqueId());
            if (carOwner.getAltPhone() != null) eco.setAltPhone(carOwner.getAltPhone());
            if (carOwner.getModel() != null) eco.setModel(carOwner.getModel());
            if (carOwner.getLicensePlate() != null) eco.setLicensePlate(carOwner.getLicensePlate());
            if (carOwner.getEngineCapacity() != null) eco.setEngineCapacity(carOwner.getEngineCapacity());
            if (carOwner.getColor() != null) eco.setColor(carOwner.getColor());

            if (carOwner.getAutomobile() != null) {
                AutoMobiles auto = automobilesRepository.findById(carOwner.getAutomobile().getId())
                        .orElseThrow(() -> new RuntimeException("Automobile with that ID does not exist"));
                eco.setAutomobile(auto);
            }

            if (carOwner.getProfilePic() != null && carOwner.getProfilePic().length > 0)
                eco.setProfilePic(carOwner.getProfilePic());

            return carOwnerRepository.save(eco);
        } else {
            throw new ResourceAccessException("Car Owner does not exist");
        }
    }

    @Override
    public String deleteCarOwner(Long id) {
        Optional<CarOwner> existingCarOwner = carOwnerRepository.findById(id);
        if (existingCarOwner.isPresent()) {
            carOwnerRepository.deleteById(id);
            return "Car Owner Deleted";
        } else {
            return "No Car Owner with that id";
        }
    }

    @Override
    public Optional<CarOwner> getCarOwnerByUniqueId(Integer uniqueId) {
        return carOwnerRepository.findByUniqueId(uniqueId);
    }
}
