package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.mapper.CarOwnerMapper;
import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.CarOwnerRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.CarOwnerService;
import io.jsonwebtoken.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CarOwnerServiceImpl implements CarOwnerService {

    private final CarOwnerRepository carOwnerRepository;
    private final AuthenticationService authenticationService;
    private final CarOwnerMapper mapper;

    public CarOwnerServiceImpl(CarOwnerRepository carOwnerRepository, AuthenticationService authenticationService, CarOwnerMapper mapper) {
        this.carOwnerRepository = carOwnerRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
    }

    @Override
    public CarOwner createCarOwner(CarOwnerRequestsDTO carOwnerRequestsDTO, MultipartFile profilePic) throws java.io.IOException{

        CarOwner carOwner = mapper.toEntity(carOwnerRequestsDTO);

        User userid = authenticationService.getCurrentUser();
        carOwner.setUser(userid);

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

        if (profilePic != null && !profilePic.isEmpty()) {
            carOwner.setProfilePic(profilePic.getBytes());
        }

        carOwner.setUniqueId(uniqueCarOwnerId);
        return carOwnerRepository.save(carOwner);
    }
    @Override
    public Optional<CarOwnerResponseDTO> findByUserId(Long userId) {
        Optional<CarOwner> carOwner = carOwnerRepository.findByUserId(userId);
        return carOwner.map(mapper::toDto);
    }

     @Override
    public boolean isDetailsCompleted(Long userId) {
        return carOwnerRepository.findByUserId(userId)
                .map(CarOwner::isComplete)
                .orElse(false);
    }

    @Override
    public List<CarOwnerResponseDTO> getAllCarOwners() {
        List<CarOwner> carOwners =  carOwnerRepository.findAll();
        return carOwners.stream().map(mapper::toDto).toList();
    }

    @Override
    public CarOwnerResponseDTO updateCarOwner(Long id, CarOwnerRequestsDTO carOwnerRequestsDTO) {
        Optional<CarOwner> existingCarOwnerOptional = carOwnerRepository.findById(id);

        if (existingCarOwnerOptional.isPresent()) {
            CarOwner eco = existingCarOwnerOptional.get();

            if (carOwnerRequestsDTO.getAltPhone() != null) eco.setAltPhone(carOwnerRequestsDTO.getAltPhone());
            if (carOwnerRequestsDTO.getModel() != null) eco.setModel(carOwnerRequestsDTO.getModel());
            if (carOwnerRequestsDTO.getLicensePlate() != null) eco.setLicensePlate(carOwnerRequestsDTO.getLicensePlate());
            if (carOwnerRequestsDTO.getEngineCapacity() != null) eco.setEngineCapacity(carOwnerRequestsDTO.getEngineCapacity());
            if (carOwnerRequestsDTO.getColor() != null) eco.setColor(carOwnerRequestsDTO.getColor());
            if (carOwnerRequestsDTO.getMake() != null) eco.setMake(carOwnerRequestsDTO.getMake());
            if (carOwnerRequestsDTO.getYear() != null) eco.setYear(carOwnerRequestsDTO.getYear());
            if (carOwnerRequestsDTO.getEngineType() != null) eco.setEngineType(carOwnerRequestsDTO.getEngineType());
            if (carOwnerRequestsDTO.getTransmission() != null) eco.setTransmission(carOwnerRequestsDTO.getTransmission());
            if (carOwnerRequestsDTO.getSeverity() != null) eco.setSeverity(carOwnerRequestsDTO.getSeverity());
            if (carOwnerRequestsDTO.getProfilePic() != null) eco.setProfilePic(carOwnerRequestsDTO.getProfilePic());

            CarOwner carOwnerUpdate = carOwnerRepository.save(eco);
            return mapper.toDto(carOwnerUpdate);
        } else {
            throw new ResourceAccessException("Car Owner does not exist");
        }
    }

    //connect it such that it updates dynamically
    @Override
    public CarOwnerResponseDTO updateProfilePic(Integer carOwnerUniqueId, MultipartFile profilePic) throws java.io.IOException {
        CarOwner exco = carOwnerRepository.findByUniqueId(carOwnerUniqueId)
                .orElseThrow(()-> new ResourceAccessException("Car Owner not found with this id"));
        if (profilePic != null && !profilePic.isEmpty()){
            exco.setProfilePic(profilePic.getBytes());
        }else {
            exco.setProfilePic(null);
        }
        CarOwner updatedEntity = carOwnerRepository.save(exco);
        return mapper.toDto(updatedEntity);
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
    public Optional<CarOwnerResponseDTO> getCarOwnerByUniqueId(Integer uniqueId) {
        Optional<CarOwner> carOwner = carOwnerRepository.findByUniqueId(uniqueId);
        return carOwner.map(mapper::toDto);
    }
}
