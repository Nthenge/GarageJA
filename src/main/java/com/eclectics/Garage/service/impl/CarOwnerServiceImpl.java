package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.CarOwnerLocationUpdateDTO;
import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.exception.GarageExceptions;
import com.eclectics.Garage.mapper.CarOwnerMapper;
import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.Location;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.CarOwnerRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.CarOwnerService;
import com.eclectics.Garage.service.OSSService;
import com.eclectics.Garage.specificationExecutor.CarOwnerSpecificationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@CacheConfig(cacheNames = {"carOwners"})
public class CarOwnerServiceImpl implements CarOwnerService {

    private static final Logger logger = LoggerFactory.getLogger(CarOwnerServiceImpl.class);

    private final CarOwnerRepository carOwnerRepository;
    private final AuthenticationService authenticationService;
    private final CarOwnerMapper mapper;
    private final OSSService ossService;

    private final Map<Long, CarOwnerResponseDTO> carOwnerCacheByUserId = new ConcurrentHashMap<>();
    private final Map<Integer, CarOwnerResponseDTO> carOwnerCacheByUniqueId = new ConcurrentHashMap<>();
    private final Map<Long, ProfileCompleteDTO> profileCompletionCache = new ConcurrentHashMap<>();
    private final Set<Integer> generatedIds = Collections.synchronizedSet(new HashSet<>());

    public CarOwnerServiceImpl(CarOwnerRepository carOwnerRepository, AuthenticationService authenticationService, CarOwnerMapper mapper, OSSService ossService) {
        this.carOwnerRepository = carOwnerRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.ossService = ossService;
    }

    public ProfileCompleteDTO checkProfileCompletion(CarOwner carOwner) {
        if (profileCompletionCache.containsKey(carOwner.getId())) {
            logger.debug("[CACHE HIT] Profile completion for CarOwner ID={}", carOwner.getId());
            return profileCompletionCache.get(carOwner.getId());
        }

        List<String> missingFields = carOwner.getMissingFields();
        boolean isComplete = missingFields.isEmpty();

        ProfileCompleteDTO dto = new ProfileCompleteDTO(isComplete, missingFields);
        profileCompletionCache.put(carOwner.getId(), dto);

        logger.info("[PROFILE CHECK] CarOwner ID={} → complete={}", carOwner.getId(), isComplete);
        return dto;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    @Override
    @CacheEvict(value = {"allCarOwners"}, allEntries = true)
    public CarOwner createCarOwner(CarOwnerRequestsDTO carOwnerRequestsDTO, MultipartFile profilePic) throws IOException {
        logger.info("[CREATE] Creating CarOwner with alt phone={}", carOwnerRequestsDTO.getAltPhone());

        CarOwner carOwner = mapper.toEntity(carOwnerRequestsDTO);
        User user = authenticationService.getCurrentUser();
        carOwner.setUser(user);

        Random random = new Random();
        Integer uniqueCarOwnerId;
        do {
            uniqueCarOwnerId = random.nextInt(8888889) + 1111111;
        } while (generatedIds.contains(uniqueCarOwnerId) || carOwnerRepository.findByUniqueId(uniqueCarOwnerId).isPresent());

        generatedIds.add(uniqueCarOwnerId);

        if (profilePic != null && !profilePic.isEmpty()) {
            String fileExtension = getFileExtension(profilePic.getOriginalFilename());
            String uniqueFileName = "car-owner-profiles/" + user.getId() + "-" + UUID.randomUUID() + fileExtension;

            String fileUrl = ossService.uploadFile(uniqueFileName, profilePic.getInputStream());
            carOwner.setProfilePic(fileUrl);
            logger.debug("[CREATE] Profile picture uploaded: {}", fileUrl);
        }

        carOwner.setUniqueId(uniqueCarOwnerId);
        CarOwner saved = carOwnerRepository.save(carOwner);
        logger.info("[CREATE SUCCESS] CarOwner created with uniqueId={} for user={}", uniqueCarOwnerId, user.getEmail());

        CarOwnerResponseDTO dto = mapper.toDto(saved);
        carOwnerCacheByUserId.put(user.getId(), dto);
        carOwnerCacheByUniqueId.put(uniqueCarOwnerId, dto);

        return saved;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "allCarOwners"
    )
    @Override
    public List<CarOwnerResponseDTO> filterCarOwners(
            String licensePlate,
            Integer uniqueId
    ) {

        Specification<CarOwner> spec = Specification.allOf(
                CarOwnerSpecificationExecutor.CarOwnerPlateContains(licensePlate),
                CarOwnerSpecificationExecutor.carOwnerUrlContains(uniqueId)
        );

        List<CarOwner> carOwners = carOwnerRepository.findAll(spec);

        if (carOwners.isEmpty()) {
            throw new GarageExceptions.ResourceNotFoundException("No CarOwners match the given criteria.");
        }

        List<CarOwnerResponseDTO> dtos = carOwners.stream()
                .map(mapper::toDto)
                .toList();


        return dtos;
    }


    @Override
    public boolean isDetailsCompleted(Long userId) {
        boolean completed = carOwnerRepository.findByUserId(userId)
                .map(CarOwner::isComplete)
                .orElse(false);
        logger.info("[DETAILS CHECK] userId={} → completed={}", userId, completed);
        return completed;
    }

    private String getObjectNameFromUrl(String fileUrl, String bucketName, String endpoint) {
        String baseUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/";
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        return null;
    }

    @Override
    @CachePut(value = "carOwnerByUser", key = "#result.user.id")
    @CacheEvict(value = {"allCarOwners", "carOwnerByUniqueId"}, allEntries = true)
    public CarOwnerResponseDTO updateOwnProfile(CarOwnerRequestsDTO carOwnerRequestsDTO,
                                                MultipartFile profilePic) throws IOException {
        logger.info("[UPDATE] Updating profile for currently authenticated CarOwner");

        User user = authenticationService.getCurrentUser();
        CarOwner eco = carOwnerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceAccessException("Car Owner profile not found for user: " + user.getEmail()));

        if (profilePic != null && !profilePic.isEmpty()) {
            String oldUrl = eco.getProfilePic();
            if (oldUrl != null && !oldUrl.isBlank()) {
                try {
                    String objectName = getObjectNameFromUrl(oldUrl, ossService.getBucketName(), ossService.getEndpoint());
                    if (objectName != null) {
                        ossService.deleteFile(objectName);
                        logger.debug("[UPDATE] Old profile picture deleted: {}", objectName);
                    }
                } catch (Exception e) {
                    logger.warn("[UPDATE] Failed to delete old profile picture: {}", e.getMessage());
                }
            }

            String fileExtension = getFileExtension(profilePic.getOriginalFilename());
            String uniqueFileName = "car-owner-profiles/" + eco.getId() + "-" + UUID.randomUUID() + fileExtension;
            String fileUrl = ossService.uploadFile(uniqueFileName, profilePic.getInputStream());
            eco.setProfilePic(fileUrl);
            logger.debug("[UPDATE] New profile picture uploaded: {}", fileUrl);
        }

        CarOwner updated = carOwnerRepository.save(eco);
        CarOwnerResponseDTO dto = mapper.toDto(updated);

        carOwnerCacheByUserId.put(user.getId(), dto);
        logger.info("[UPDATE SUCCESS] CarOwner profile updated for user={}", user.getEmail());
        return dto;
    }

    // Inside CarOwnerServiceImpl

    @Transactional
    @Override
    public CarOwner updateCarOwnerLiveLocation(Long carOwnerId, CarOwnerLocationUpdateDTO locationDto) {

        CarOwner owner = carOwnerRepository.findById(carOwnerId)
                .orElseThrow(() -> {
                    logger.warn("Attempted to update location for non-existent CarOwner ID: {}", carOwnerId);
                    return new GarageExceptions.ResourceNotFoundException("CarOwner not found with id: " + carOwnerId);
                });

        Location newLocation = new Location();
        newLocation.setLatitude(locationDto.getLatitude());
        newLocation.setLongitude(locationDto.getLongitude());

        owner.setLiveLocation(newLocation);
        logger.info("[LIVE LOCATION] Updated live location for CarOwner ID {} to: ({}, {})",
                carOwnerId, locationDto.getLatitude(), locationDto.getLongitude());

        return carOwnerRepository.save(owner);
    }

    @Override
    @CacheEvict(value = {"allCarOwners", "carOwnerByUniqueId", "carOwnerByUser"}, allEntries = true)
    public String deleteCarOwner(Long id) {
        logger.info("[DELETE] Attempting to delete CarOwner with id={}", id);

        Optional<CarOwner> existingCarOwner = carOwnerRepository.findById(id);
        if (existingCarOwner.isPresent()) {
            CarOwner carOwner = existingCarOwner.get();
            carOwnerCacheByUserId.remove(carOwner.getUser().getId());
            carOwnerCacheByUniqueId.remove(carOwner.getUniqueId());

            carOwnerRepository.deleteById(id);
            logger.info("[DELETE SUCCESS] CarOwner deleted with id={}", id);
            return "Car Owner Deleted";
        } else {
            logger.warn("[DELETE FAILED] No CarOwner found with id={}", id);
            return "No Car Owner with that id";
        }
    }

    @Override
    public Optional<String> getProfilePictureUrlByUniqueId(Integer uniqueId, int expiryMinutes) {
        logger.info("[OSS URL] Generating presigned URL for CarOwner uniqueId={}", uniqueId);

        return carOwnerRepository.findByUniqueId(uniqueId)
                .map(carOwner -> {
                    String objectKey = carOwner.getProfilePic();
                    if (objectKey == null || objectKey.isBlank()) {
                        logger.warn("[OSS URL] No profile picture found for CarOwner uniqueId={}", uniqueId);
                        return null;
                    }
                    return ossService.generatePresignedUrl(objectKey, expiryMinutes);
                });
    }
}
