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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CarOwnerServiceImpl.class);

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
        boolean isComplete = missingFields.isEmpty();
        logger.info("[PROFILE CHECK] CarOwner ID={} → complete={}", carOwner.getId(), isComplete);
        return new ProfileCompleteDTO(isComplete, missingFields);
    }

    @Override
    @CacheEvict(value = {"allCarOwners"}, allEntries = true)
    public CarOwner createCarOwner(CarOwnerRequestsDTO carOwnerRequestsDTO, MultipartFile profilePic) throws java.io.IOException {
        logger.info("[CREATE] Creating CarOwner with alt phone={}", carOwnerRequestsDTO.getAltPhone());

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
            logger.debug("[CREATE] Profile picture uploaded for user={}", user.getEmail());
        }

        carOwner.setUniqueId(uniqueCarOwnerId);
        CarOwner saved = carOwnerRepository.save(carOwner);
        logger.info("[CREATE SUCCESS] CarOwner created with uniqueId={} for user={}", uniqueCarOwnerId, user.getEmail());

        return saved;
    }

    @Override
    @Cacheable(value = "carOwnerByUser", key = "#userId")
    public Optional<CarOwnerResponseDTO> findByUserId(Long userId) {
        logger.info("[FETCH] Fetching CarOwner by userId={}", userId);
        return carOwnerRepository.findByUserId(userId).map(mapper::toDto);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        boolean completed = carOwnerRepository.findByUserId(userId)
                .map(CarOwner::isComplete)
                .orElse(false);
        logger.info("[DETAILS CHECK] userId={} → completed={}", userId, completed);
        return completed;
    }

    @Override
    @Cacheable(value = "allCarOwners")
    public List<CarOwnerResponseDTO> getAllCarOwners() {
        logger.info("[FETCH ALL] Retrieving all CarOwners");
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
        logger.info("[UPDATE] Updating CarOwner profile for uniqueId={}", carOwnerUniqueId);

        Optional<CarOwner> existingCarOwnerOptional = carOwnerRepository.findByUniqueId(carOwnerUniqueId);

        if (existingCarOwnerOptional.isPresent()) {
            CarOwner eco = existingCarOwnerOptional.get();

            if (carOwnerRequestsDTO.getAltPhone() != null) logger.debug("[UPDATE] altPhone={}", carOwnerRequestsDTO.getAltPhone());
            if (carOwnerRequestsDTO.getLicensePlate() != null) logger.debug("[UPDATE] licensePlate={}", carOwnerRequestsDTO.getLicensePlate());
            if (carOwnerRequestsDTO.getMake() != null) logger.debug("[UPDATE] make={}", carOwnerRequestsDTO.getMake());

            if (profilePic != null && !profilePic.isEmpty()) {
                eco.setProfilePic(profilePic.getBytes());
                logger.debug("[UPDATE] Profile picture updated for carOwnerUniqueId={}", carOwnerUniqueId);
            }

            CarOwner updatedCarOwner = carOwnerRepository.save(eco);
            logger.info("[UPDATE SUCCESS] CarOwner updated with uniqueId={}", carOwnerUniqueId);
            return mapper.toDto(updatedCarOwner);
        } else {
            logger.error("[UPDATE FAILED] CarOwner with uniqueId={} not found", carOwnerUniqueId);
            throw new ResourceAccessException("Car Owner does not exist");
        }
    }

    @Override
    @CacheEvict(value = {"allCarOwners", "carOwnerByUniqueId", "carOwnerByUser"}, allEntries = true)
    public String deleteCarOwner(Long id) {
        logger.info("[DELETE] Attempting to delete CarOwner with id={}", id);

        Optional<CarOwner> existingCarOwner = carOwnerRepository.findById(id);
        if (existingCarOwner.isPresent()) {
            carOwnerRepository.deleteById(id);
            logger.info("[DELETE SUCCESS] CarOwner deleted with id={}", id);
            return "Car Owner Deleted";
        } else {
            logger.warn("[DELETE FAILED] No CarOwner found with id={}", id);
            return "No Car Owner with that id";
        }
    }

    @Override
    @Cacheable(value = "carOwnerByUniqueId", key = "#uniqueId")
    public Optional<CarOwnerResponseDTO> getCarOwnerByUniqueId(Integer uniqueId) {
        logger.info("[FETCH] Fetching CarOwner by uniqueId={}", uniqueId);
        return carOwnerRepository.findByUniqueId(uniqueId).map(mapper::toDto);
    }
}
