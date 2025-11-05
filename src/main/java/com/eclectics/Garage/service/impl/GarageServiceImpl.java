package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.mapper.GarageMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.GarageService;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import com.eclectics.Garage.service.OSSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {"garages"})
public class GarageServiceImpl implements GarageService {

    private static final Logger logger = LoggerFactory.getLogger(GarageServiceImpl.class);

    private final GarageRepository garageRepository;
    private final AuthenticationService authenticationService;
    private final GarageMapper mapper;
    private final OSSService ossService;

    public GarageServiceImpl(GarageRepository garageRepository, AuthenticationService authenticationService, GarageMapper mapper, OSSService ossService) {
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.ossService = ossService;
    }

    public ProfileCompleteDTO checkProfileCompletion(Garage garage) {
        logger.debug("Checking profile completion for garage ID: {}", garage.getGarageId());
        List<String> missingFields = garage.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    @Override
    @CacheEvict(value = {"allGarages"}, allEntries = true)
    public Garage createGarage(GarageRequestsDTO garageRequestsDTO, MultipartFile businessLicense,
                               MultipartFile professionalCertificate, MultipartFile facilityPhotos) throws IOException {

        logger.info("Creating new garage with name: {}", garageRequestsDTO.getBusinessName());

        Garage garage = mapper.toEntity(garageRequestsDTO);
        User user = authenticationService.getCurrentUser();
        garage.setUser(user);
        logger.debug("Linked garage to user: {}", user.getEmail());

        Optional<Garage> existingGarage = garageRepository.findByBusinessName(garage.getBusinessName());
        if (existingGarage.isPresent()) {
            logger.warn("Garage creation failed - name '{}' already exists", garage.getBusinessName());
            throw new ResourceNotFoundException("Garage with this name exists");
        }

        try {
            if (businessLicense != null && !businessLicense.isEmpty()) {
                String fileExtension = getFileExtension(businessLicense.getOriginalFilename());
                String uniqueFileName = "Garages/licences/" + user.getId() + "-" + UUID.randomUUID().toString() + fileExtension;

                String fileUrl = ossService.uploadFile(uniqueFileName, businessLicense.getInputStream());
                garage.setBusinessLicense(fileUrl);

                logger.debug("Attached business license file: {}", fileUrl);
            }
            if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
                String fileExtension = getFileExtension(professionalCertificate.getOriginalFilename());
                String uniqueFileName = "Garages/profCerts/" + user.getId() + "-" + UUID.randomUUID().toString() + fileExtension;

                String fileUrl = ossService.uploadFile(uniqueFileName, professionalCertificate.getInputStream());
                garage.setProfessionalCertificate(fileUrl);

                logger.debug("Attached professional certificate: {}", fileUrl);
            }
            if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
                String fileExtension = getFileExtension(facilityPhotos.getOriginalFilename());
                String uniqueFileName = "Garages/photos/" + user.getId() + "-" + UUID.randomUUID().toString() + fileExtension;

                String fileUrl = ossService.uploadFile(uniqueFileName, facilityPhotos.getInputStream());
                garage.setFacilityPhotos(fileUrl);

                logger.debug("Attached facility photos: {}", fileUrl);
            }
        } catch (IOException e) {
            logger.error("Failed to read uploaded files during garage creation: {}", e.getMessage());
            throw new BadRequestException("Failed to process uploaded files");
        }

        // Generate a unique garage ID
        boolean uniqueAdminIdExists;
        long uniqueAdminId;
        do {
            Random random = new Random();
            uniqueAdminId = random.nextInt(90000) + 10000;
            uniqueAdminIdExists = garageRepository.findByGarageId(uniqueAdminId).isPresent();
        } while (uniqueAdminIdExists);

        garage.setGarageId(uniqueAdminId);
        logger.debug("Generated unique garage ID: {}", uniqueAdminId);

        Garage savedGarage = garageRepository.save(garage);
        logger.info("Garage '{}' created successfully with ID: {}", savedGarage.getBusinessName(), savedGarage.getGarageId());
        return savedGarage;
    }

    @Override
    @Cacheable(value = "garageByUser", key = "#userId")
    public Optional<GarageResponseDTO> findByUserId(Long userId) {
        logger.info("Fetching garage for user ID: {}", userId);
        Optional<Garage> garage = garageRepository.findByUserId(userId);
        if (garage.isEmpty()) {
            logger.warn("No garage found for user ID: {}", userId);
        }
        return garage.map(mapper::toResponseDTO);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        logger.debug("Checking garage detail completion for user ID: {}", userId);
        return garageRepository.findByUserId(userId)
                .map(Garage::isComplete)
                .orElse(false);
    }

    @Override
    @Cacheable(value = "garageById", key = "#garageId")
    public Optional<GarageResponseDTO> getGarageById(Long garageId) {
        logger.info("Fetching garage by ID: {}", garageId);
        Optional<Garage> garage = garageRepository.findByGarageId(garageId);
        if (garage.isEmpty()) {
            logger.warn("Garage not found with ID: {}", garageId);
        }
        return garage.map(mapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "garageByName", key = "#name")
    public Optional<GarageResponseDTO> getGarageByName(String name) {
        logger.info("Fetching garage by name: {}", name);
        Optional<Garage> garage = garageRepository.findByBusinessName(name);
        if (garage.isEmpty()) {
            logger.warn("Garage not found with name: {}", name);
        }
        return garage.map(mapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "allGarages")
    public List<GarageResponseDTO> getAllGarages() {
        logger.info("Fetching all garages");
        List<Garage> garages = garageRepository.findAll();
        logger.debug("Found {} garages in total", garages.size());
        return mapper.toResponseDTOList(garages);
    }

    @Override
    public long countAllGarages() {
        long count = garageRepository.count();
        logger.debug("Total number of garages: {}", count);
        return count;
    }

    private String getObjectNameFromUrl(String fileUrl, String bucketName, String endpoint) {
        String baseUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/";
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        return null; // The URL format is unexpected
    }

    @Override
    @CachePut(value = "garageById", key = "#garageId")
    @CacheEvict(value = {"allGarages", "garageByUser", "garageByName"}, allEntries = true)
    public GarageResponseDTO updateGarage(Long garageId, GarageRequestsDTO garageRequestsDTO,
                                          MultipartFile businessLicense, MultipartFile professionalCertificate,
                                          MultipartFile facilityPhotos) {
        return garageRepository.findByGarageId(garageId).map(existingGarage -> {

            if (garageRequestsDTO.getBusinessName() != null)
                existingGarage.setBusinessName(garageRequestsDTO.getBusinessName());
            if (garageRequestsDTO.getOperatingHours() != null)
                existingGarage.setOperatingHours(garageRequestsDTO.getOperatingHours());
            if (garageRequestsDTO.getBusinessEmailAddress() != null)
                existingGarage.setBusinessEmailAddress(garageRequestsDTO.getBusinessEmailAddress());
            if (garageRequestsDTO.getBusinessRegNumber() != null)
                existingGarage.setBusinessRegNumber(garageRequestsDTO.getBusinessRegNumber());
            if (garageRequestsDTO.getTwentyFourHours() != null)
                existingGarage.setTwentyFourHours(garageRequestsDTO.getTwentyFourHours());
            if (garageRequestsDTO.getServiceCategories() != null)
                existingGarage.setServiceCategories(garageRequestsDTO.getServiceCategories());
            if (garageRequestsDTO.getSpecialisedServices() != null)
                existingGarage.setSpecialisedServices(garageRequestsDTO.getSpecialisedServices());
            if (garageRequestsDTO.getPhysicalBusinessAddress() != null)
                existingGarage.setPhysicalBusinessAddress(garageRequestsDTO.getPhysicalBusinessAddress());
            if (garageRequestsDTO.getBusinessPhoneNumber() != null)
                existingGarage.setBusinessPhoneNumber(garageRequestsDTO.getBusinessPhoneNumber());
            if (garageRequestsDTO.getYearsInOperation() != null)
                existingGarage.setYearsInOperation(garageRequestsDTO.getYearsInOperation());
            if (garageRequestsDTO.getMpesaPayBill() != null)
                existingGarage.setMpesaPayBill(garageRequestsDTO.getMpesaPayBill());
            if (garageRequestsDTO.getMpesaTill() != null)
                existingGarage.setMpesaTill(garageRequestsDTO.getMpesaTill());

            try {
                if (businessLicense != null && !businessLicense.isEmpty()) {
                    String oldUrl = existingGarage.getBusinessLicense();
                    if (oldUrl != null && !oldUrl.isBlank()) {
                        try {
                            String objectName = getObjectNameFromUrl(oldUrl, ossService.getBucketName(), ossService.getEndpoint());
                            if (objectName != null) {
                                ossService.deleteFile(objectName);
                                logger.debug("[UPDATE] Old business license deleted from OSS: {}", objectName);
                            }
                        } catch (Exception e) {
                            logger.warn("[UPDATE] Failed to delete old business license from OSS: {}", e.getMessage());
                        }
                    }
                    String fileExtension = getFileExtension(businessLicense.getOriginalFilename());
                    String uniqueFileName = "Garages/licences/" + existingGarage.getId() + "-" + UUID.randomUUID().toString() + fileExtension;
                    String fileUrl = ossService.uploadFile(uniqueFileName, businessLicense.getInputStream());
                    existingGarage.setBusinessLicense(fileUrl);

                    logger.debug("[UPDATE] New business license uploaded to OSS at: {}", fileUrl);

                }
                if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
                    String oldUrl = existingGarage.getProfessionalCertificate();
                    if (oldUrl != null && !oldUrl.isBlank()) {
                        try {
                            String objectName = getObjectNameFromUrl(oldUrl, ossService.getBucketName(), ossService.getEndpoint());
                            if (objectName != null) {
                                ossService.deleteFile(objectName);
                                logger.debug("[UPDATE] Old professional certificate  deleted from OSS: {}", objectName);
                            }
                        } catch (Exception e) {
                            logger.warn("[UPDATE] Failed to delete old professional certificate  from OSS: {}", e.getMessage());
                        }
                    }
                    String fileExtension = getFileExtension(professionalCertificate.getOriginalFilename());
                    String uniqueFileName = "Garages/profCerts/" + existingGarage.getId() + "-" + UUID.randomUUID().toString() + fileExtension;
                    String fileUrl = ossService.uploadFile(uniqueFileName, professionalCertificate.getInputStream());
                    existingGarage.setProfessionalCertificate(fileUrl);

                    logger.debug("[UPDATE] New professional certificate  uploaded to OSS at: {}", fileUrl);

                }
                if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
                    String oldUrl = existingGarage.getFacilityPhotos();
                    if (oldUrl != null && !oldUrl.isBlank()) {
                        try {
                            String objectName = getObjectNameFromUrl(oldUrl, ossService.getBucketName(), ossService.getEndpoint());
                            if (objectName != null) {
                                ossService.deleteFile(objectName);
                                logger.debug("[UPDATE] Old profile garage deleted from OSS: {}", objectName);
                            }
                        } catch (Exception e) {
                            logger.warn("[UPDATE] Failed to delete old garage picture from OSS: {}", e.getMessage());
                        }
                    }
                    String fileExtension = getFileExtension(facilityPhotos.getOriginalFilename());
                    String uniqueFileName = "Garages/photos/" + existingGarage.getId() + "-" + UUID.randomUUID().toString() + fileExtension;
                    String fileUrl = ossService.uploadFile(uniqueFileName, facilityPhotos.getInputStream());
                    existingGarage.setFacilityPhotos(fileUrl);

                    logger.debug("[UPDATE] New Garage facility picture uploaded to OSS at: {}", fileUrl);

                }
            } catch (IOException e) {
                logger.error("Failed to process uploaded files for garage update (ID {}): {}", garageId, e.getMessage());
                throw new BadRequestException("Failed to read file data");
            }

            Garage updatedGarage = garageRepository.save(existingGarage);
            logger.info("Garage with ID {} updated successfully", garageId);
            return mapper.toResponseDTO(updatedGarage);

        }).orElseThrow(() -> {
            logger.error("Garage not found during update, ID: {}", garageId);
            return new ResourceNotFoundException("Garage not found");
        });
    }

    @Override
    @CacheEvict(value = {"allGarages", "garageById", "garageByName", "garageByUser"}, allEntries = true)
    public void deleteGarage(Long id) {
        logger.info("Deleting garage with ID: {}", id);

        if (!garageRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent garage with ID: {}", id);
            throw new ResourceNotFoundException("Garage with id " + id + " does not exist");
        }

        garageRepository.deleteById(id);
        logger.info("Garage with ID {} deleted successfully", id);
    }

    @Override
    public Optional<String> getGarageUrlByUniqueId(Long uniqueId, int expiryMinutes) {
        logger.info("[OSS URL] Generating presigned URL for Garage uniqueId={}", uniqueId);

        return garageRepository.findByGarageId(uniqueId)
                .map(garage -> {
                    String objectKey = garage.getBusinessLicense();

                    if (objectKey == null || objectKey.isBlank()) {
                        logger.warn("[OSS URL] No Business License key found for CarOwner uniqueId={}", uniqueId);
                        return null;
                    }

                    String presignedUrl = ossService.generatePresignedUrl(objectKey, expiryMinutes);

                    logger.debug("[OSS URL] Generated URL for {} will expire in {} minutes", objectKey, expiryMinutes);
                    return presignedUrl;
                });
    }
}
