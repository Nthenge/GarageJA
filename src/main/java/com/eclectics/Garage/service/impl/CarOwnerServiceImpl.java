package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.repository.CarOwnerRepository;
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

    public CarOwnerServiceImpl(CarOwnerRepository carOwnerRepository) {
        this.carOwnerRepository = carOwnerRepository;
    }

    @Override
    public CarOwner createCarOwner(CarOwner carOwner) {
            Optional<CarOwner> carOwnerExists = carOwnerRepository.findByUniqueId(carOwner.getUniqueId());
            if (carOwnerExists.isPresent()){
                throw new RuntimeException("This car owner exist.");
            }

            boolean uniqueCarOwnerExists;
            Integer uniqueCarOwnerId;

            do {
                Random random = new Random();
                uniqueCarOwnerId = random.nextInt(8888889) + 1111111;

                uniqueCarOwnerExists = carOwnerRepository.findByUniqueId(uniqueCarOwnerId).isPresent();
                if (uniqueCarOwnerExists){
                    throw new RuntimeException("A car owner with this id already exist");
                }

            }while (uniqueCarOwnerExists);

            carOwner.setUniqueId(uniqueCarOwnerId);

        return carOwnerRepository.save(carOwner);
    }

    @Override
    public CarOwner uploadDocument(Integer uniqueId, MultipartFile profilePic) throws java.io.IOException {
        CarOwner carOwner = carOwnerRepository.findByUniqueId(uniqueId).orElseThrow(()-> new RuntimeException("Car owner not found"));

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
            // eco = existing car owner
            CarOwner eco = existingCarOwnerOptional.get();

            if (carOwner.getUniqueId() != null) eco.setUniqueId(carOwner.getUniqueId());
            if (carOwner.getAltPhone() != null) eco.setAltPhone(carOwner.getAltPhone());
            if (carOwner.getMake() != null) eco.setMake(carOwner.getMake());
            if (carOwner.getModel() != null) eco.setModel(carOwner.getModel());
            if (carOwner.getYear() != null) eco.setYear(carOwner.getYear());
            if (carOwner.getLicensePlate() != null) eco.setLicensePlate(carOwner.getLicensePlate());
            if (carOwner.getEngineType() != null) eco.setEngineType(carOwner.getEngineType());
            if (carOwner.getEngineCapacity() != null) eco.setEngineCapacity(carOwner.getEngineCapacity());
            if (carOwner.getColor() != null) eco.setColor(carOwner.getColor());
            if (carOwner.getTransmission() != null) eco.setTransmission(carOwner.getTransmission());
            if (carOwner.getSeverity() != null) eco.setSeverity(carOwner.getSeverity());

            //binary data
            if (carOwner.getProfilePic() != null && carOwner.getProfilePic().length > 0) eco.setProfilePic(carOwner.getProfilePic());

            return carOwnerRepository.save(eco);
        }else {
            throw new ResourceAccessException("Car Owner does not exist");
        }
    }

    @Override
    public String deleteCarOwner(Long id) {
        Optional<CarOwner> existingCarOwner = carOwnerRepository.findById(id);
        if (existingCarOwner.isPresent()){
            carOwnerRepository.deleteById(id);
            return "Car Owner Deleted";
        }else {
            return "No Car Owner with that id";
        }
    }

    @Override
    public Optional<CarOwner> getCarOwnerByUniqueId(Integer uniqueId) {
        return carOwnerRepository.findByUniqueId(uniqueId);
    }
}
