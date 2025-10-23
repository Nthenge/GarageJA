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
import com.eclectics.Garage.service.OSSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {"carOwners"})
public class CarOwnerServiceImpl implements CarOwnerService {

    private static final Logger logger = LoggerFactory.getLogger(CarOwnerServiceImpl.class);

    private final CarOwnerRepository carOwnerRepository;
    private final AuthenticationService authenticationService;
    private final CarOwnerMapper mapper;
    private final OSSService ossService;

    public CarOwnerServiceImpl(CarOwnerRepository carOwnerRepository, AuthenticationService authenticationService, CarOwnerMapper mapper, OSSService ossService) {
        this.carOwnerRepository = carOwnerRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.ossService = ossService;
    }

    public ProfileCompleteDTO checkProfileCompletion(CarOwner carOwner) {
        List<String> missingFields = carOwner.getMissingFields();
        boolean isComplete = missingFields.isEmpty();
        logger.info("[PROFILE CHECK] CarOwner ID={} → complete={}", carOwner.getId(), isComplete);
        return new ProfileCompleteDTO(isComplete, missingFields);
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
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
            String fileExtension = getFileExtension(profilePic.getOriginalFilename());
            String uniqueFileName = "car-owner-profiles/" + user.getId() + "-" + UUID.randomUUID().toString() + fileExtension;

            String fileUrl = ossService.uploadFile(uniqueFileName, profilePic.getInputStream());
            carOwner.setProfilePic(fileUrl);

            logger.debug("[CREATE] Profile picture uploaded to OSS at: {}", fileUrl);
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

    private String getObjectNameFromUrl(String fileUrl, String bucketName, String endpoint) {
        String baseUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/";
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        return null; // The URL format is unexpected
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
                String oldUrl = eco.getProfilePic();
                if (oldUrl != null && !oldUrl.isBlank()) {
                    try {
                        String objectName = getObjectNameFromUrl(oldUrl, ossService.getBucketName(), ossService.getEndpoint());
                        if (objectName != null) {
                            ossService.deleteFile(objectName);
                            logger.debug("[UPDATE] Old profile picture deleted from OSS: {}", objectName);
                        }
                    } catch (Exception e) {
                        logger.warn("[UPDATE] Failed to delete old profile picture from OSS: {}", e.getMessage());
                    }
                }
                String fileExtension = getFileExtension(profilePic.getOriginalFilename());
                String uniqueFileName = "car-owner-profiles/" + eco.getId() + "-" + UUID.randomUUID().toString() + fileExtension;
                String fileUrl = ossService.uploadFile(uniqueFileName, profilePic.getInputStream());
                eco.setProfilePic(fileUrl);

                logger.debug("[UPDATE] New profile picture uploaded to OSS at: {}", fileUrl);

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

    @Override
    public Optional<String> getProfilePictureUrlByUniqueId(Integer uniqueId, int expiryMinutes) {
        logger.info("[OSS URL] Generating presigned URL for CarOwner uniqueId={}", uniqueId);

        return carOwnerRepository.findByUniqueId(uniqueId)
                .map(carOwner -> {
                    String objectKey = carOwner.getProfilePic();

                    if (objectKey == null || objectKey.isBlank()) {
                        logger.warn("[OSS URL] No profile picture key found for CarOwner uniqueId={}", uniqueId);
                        return null;
                    }

                    String presignedUrl = ossService.generatePresignedUrl(objectKey, expiryMinutes);

                    logger.debug("[OSS URL] Generated URL for {} will expire in {} minutes", objectKey, expiryMinutes);
                    return presignedUrl;
                });
    }
}
