package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.mapper.CarOwnerMapper;
import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.CarOwnerRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.CarOwnerService;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@CacheConfig(cacheNames = {"carOwners"})
public class CarOwnerServiceImpl implements CarOwnerService {

    private final CarOwnerRepository carOwnerRepository;
    private final AuthenticationService authenticationService;
    private final CarOwnerMapper mapper;

    public CarOwnerServiceImpl(CarOwnerRepository carOwnerRepository, AuthenticationService authenticationService, CarOwnerMapper mapper) {
        this.carOwnerRepository = carOwnerRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
    }

    public ProfileCompleteDTO checkProfileCompletion(CarOwner carOwner) {
        List<String> missingFields = carOwner.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    @Override
    @CacheEvict(value = {"allCarOwners"}, allEntries = true)
    public CarOwner createCarOwner(CarOwnerRequestsDTO carOwnerRequestsDTO, MultipartFile profilePic) throws java.io.IOException {

        CarOwner carOwner = mapper.toEntity(carOwnerRequestsDTO);
        User user = authenticationService.getCurrentUser();
        carOwner.setUser(user);

        boolean uniqueCarOwnerExists;
        Integer uniqueCarOwnerId;

        do {
            Random random = new Random();
            uniqueCarOwnerId = random.nextInt(8888889) + 1111111;
            uniqueCarOwnerExists = carOwnerRepository.findByUniqueId(uniqueCarOwnerId).isPresent();
        } while (uniqueCarOwnerExists);

        if (profilePic != null && !profilePic.isEmpty()) {
            carOwner.setProfilePic(profilePic.getBytes());
        }

        carOwner.setUniqueId(uniqueCarOwnerId);
        return carOwnerRepository.save(carOwner);
    }

    @Override
    @Cacheable(value = "carOwnerByUser", key = "#userId")
    public Optional<CarOwnerResponseDTO> findByUserId(Long userId) {
        return carOwnerRepository.findByUserId(userId).map(mapper::toDto);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return carOwnerRepository.findByUserId(userId)
                .map(CarOwner::isComplete)
                .orElse(false);
    }

    @Override
    @Cacheable(value = "allCarOwners")
    public List<CarOwnerResponseDTO> getAllCarOwners() {
        return carOwnerRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @CachePut(value = "carOwnerByUniqueId", key = "#carOwnerUniqueId")
    @CacheEvict(value = {"allCarOwners", "carOwnerByUser"}, allEntries = true)
    public CarOwnerResponseDTO updateProfilePic(Integer carOwnerUniqueId,
                                                CarOwnerRequestsDTO carOwnerRequestsDTO,
                                                MultipartFile profilePic) throws java.io.IOException {
        Optional<CarOwner> existingCarOwnerOptional = carOwnerRepository.findByUniqueId(carOwnerUniqueId);

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

            if (profilePic != null && !profilePic.isEmpty()) {
                eco.setProfilePic(profilePic.getBytes());
            } else {
                eco.setProfilePic(null);
            }

            CarOwner updatedCarOwner = carOwnerRepository.save(eco);
            return mapper.toDto(updatedCarOwner);
        } else {
            throw new ResourceAccessException("Car Owner does not exist");
        }
    }

    @Override
    @CacheEvict(value = {"allCarOwners", "carOwnerByUniqueId", "carOwnerByUser"}, allEntries = true)
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
    @Cacheable(value = "carOwnerByUniqueId", key = "#uniqueId")
    public Optional<CarOwnerResponseDTO> getCarOwnerByUniqueId(Integer uniqueId) {
        return carOwnerRepository.findByUniqueId(uniqueId).map(mapper::toDto);
    }
}
