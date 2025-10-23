package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.mapper.MechanicMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.MechanicService;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import com.eclectics.Garage.service.OSSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class MechanicServiceImpl implements MechanicService {

    private static final Logger logger = LoggerFactory.getLogger(MechanicServiceImpl.class);

    private final MechanicRepository mechanicRepository;
    private final GarageRepository garageRepository;
    private final AuthenticationService authenticationService;
    private final MechanicMapper mapper;
    private final OSSService ossService;

    public MechanicServiceImpl(MechanicRepository mechanicRepository, GarageRepository garageRepository,
                               AuthenticationService authenticationService, MechanicMapper mapper, OSSService ossService) {
        this.mechanicRepository = mechanicRepository;
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.ossService = ossService;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    private String getObjectNameFromUrl(String fileUrl, String bucketName, String endpoint) {
        String baseUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/";
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        return null;
    }

    public ProfileCompleteDTO checkProfileCompletion(Mechanic mechanic) {
        logger.debug("Checking profile completion for mechanic with ID: {}", mechanic.getId());
        List<String> missingFields = mechanic.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByUser", key = "#id")
    public Optional<MechanicResponseDTO> findByUserId(Long id) {
        logger.info("Fetching mechanic by user ID: {}", id);
        Optional<Mechanic> mechanic = mechanicRepository.findByUserId(id);
        if (mechanic.isEmpty()) {
            logger.warn("No mechanic found for user ID: {}", id);
        }
        return mechanic.map(mapper::toResponseDTO);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        logger.debug("Checking if mechanic details are completed for user ID: {}", userId);
        return mechanicRepository.findByUserId(userId)
                .map(Mechanic::isComplete)
                .orElse(false);
    }

    @Transactional
    @Override
    @CachePut(value = "mechanics", key = "#result.id")
    @CacheEvict(value = {"allMechanics", "mechanicsByGarage"}, allEntries = true)
    public MechanicResponseDTO createMechanic(MechanicRequestDTO mechanicRequestDTO,
                                              MultipartFile profilePic,
                                              MultipartFile nationalIDPic,
                                              MultipartFile professionalCertfificate,
                                              MultipartFile anyRelevantCertificate,
                                              MultipartFile policeClearanceCertficate) throws IOException {
        logger.info("Creating new mechanic for national ID: {}", mechanicRequestDTO.getNationalIdNumber());

        Mechanic mechanic = mapper.toEntity(mechanicRequestDTO);

        if (mechanicRepository.findMechanicByNationalIdNumber(mechanic.getNationalIdNumber()).isPresent()) {
            logger.warn("Mechanic with national ID {} already exists", mechanic.getNationalIdNumber());
            throw new ResourceNotFoundException("Mechanic with this national ID already exists");
        }

        User user = authenticationService.getCurrentUser();
        mechanic.setUser(user);
        logger.debug("Linked mechanic to user: {}", user.getEmail());

        if (mechanicRequestDTO.getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarageId())
                    .orElseThrow(() -> {
                        logger.error("Garage with ID {} not found", mechanicRequestDTO.getGarageId());
                        return new ResourceNotFoundException("Garage not found");
                    });
            mechanic.setGarage(garage);
            logger.debug("Assigned mechanic to garage: {}", garage.getBusinessName());
        }

        try {
            Long userId = user.getId();
            String baseFolder = "MechanicFiles/" + userId + "/";

            if (profilePic != null && !profilePic.isEmpty()) {
                String ext = getFileExtension(profilePic.getOriginalFilename());
                String uniqueName = baseFolder + "profilePic/" + UUID.randomUUID().toString() + ext;
                String fileUrl = ossService.uploadFile(uniqueName, profilePic.getInputStream());
                mechanic.setProfilePic(fileUrl);
                logger.debug("Attached profilePic: {}", fileUrl);
            }
            if (nationalIDPic != null && !nationalIDPic.isEmpty()) {
                String ext = getFileExtension(nationalIDPic.getOriginalFilename());
                String uniqueName = baseFolder + "nationalID/" + UUID.randomUUID().toString() + ext;
                String fileUrl = ossService.uploadFile(uniqueName, nationalIDPic.getInputStream());
                mechanic.setNationalIDPic(fileUrl);
                logger.debug("Attached nationalIDPic: {}", fileUrl);
            }
            if (professionalCertfificate != null && !professionalCertfificate.isEmpty()) {
                String ext = getFileExtension(professionalCertfificate.getOriginalFilename());
                String uniqueName = baseFolder + "profCert/" + UUID.randomUUID().toString() + ext;
                String fileUrl = ossService.uploadFile(uniqueName, professionalCertfificate.getInputStream());
                mechanic.setProfessionalCertfificate(fileUrl);
                logger.debug("Attached professionalCertfificate: {}", fileUrl);
            }
            if (anyRelevantCertificate != null && !anyRelevantCertificate.isEmpty()) {
                String ext = getFileExtension(anyRelevantCertificate.getOriginalFilename());
                String uniqueName = baseFolder + "relevantCert/" + UUID.randomUUID().toString() + ext;
                String fileUrl = ossService.uploadFile(uniqueName, anyRelevantCertificate.getInputStream());
                mechanic.setAnyRelevantCertificate(fileUrl);
                logger.debug("Attached anyRelevantCertificate: {}", fileUrl);
            }
            if (policeClearanceCertficate != null && !policeClearanceCertficate.isEmpty()) {
                String ext = getFileExtension(policeClearanceCertficate.getOriginalFilename());
                String uniqueName = baseFolder + "policeClearance/" + UUID.randomUUID().toString() + ext;
                String fileUrl = ossService.uploadFile(uniqueName, policeClearanceCertficate.getInputStream());
                mechanic.setPoliceClearanceCertficate(fileUrl);
                logger.debug("Attached policeClearanceCertficate: {}", fileUrl);
            }
        }catch (IOException e) {
            logger.error("Failed to read uploaded files during mechanic creation: {}", e.getMessage());
            throw new BadRequestException("Failed to process uploaded files");
        }

        Mechanic savedMechanic = mechanicRepository.save(mechanic);
        logger.info("Mechanic created successfully with ID: {}", savedMechanic.getId());
        return mapper.toResponseDTO(savedMechanic);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByNationalId", key = "#nationalIdNumber")
    public Optional<MechanicResponseDTO> getMechanicByNationalId(Integer nationalIdNumber) {
        logger.info("Fetching mechanic by national ID: {}", nationalIdNumber);
        Optional<Mechanic> mechanic = mechanicRepository.findMechanicByNationalIdNumber(nationalIdNumber);
        if (mechanic.isEmpty()) {
            logger.warn("No mechanic found for national ID: {}", nationalIdNumber);
        }
        return mechanic.map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "allMechanics")
    public List<MechanicResponseDTO> getAllMechanics() {
        logger.info("Fetching all mechanics");
        List<Mechanic> mechanics = mechanicRepository.findAll();
        logger.debug("Found {} mechanics", mechanics.size());
        return mapper.toResponseDTOList(mechanics);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByGarage", key = "#garageId")
    public List<MechanicResponseDTO> getMechanicsByGarageId(Long garageId) {
        logger.info("Fetching mechanics by garage ID: {}", garageId);
        List<Mechanic> garageMechanics = mechanicRepository.findByGarageId(garageId);
        logger.debug("Found {} mechanics in garage ID {}", garageMechanics.size(), garageId);
        return mapper.toResponseDTOList(garageMechanics);
    }

    private void updateSingleFile(MultipartFile newFile, String oldUrl, Consumer<String> urlSetter, String uploadPath) throws IOException {
        if (oldUrl != null && !oldUrl.isBlank()) {
            try {
                String objectName = getObjectNameFromUrl(oldUrl, ossService.getBucketName(), ossService.getEndpoint());
                if (objectName != null) {
                    ossService.deleteFile(objectName);
                    logger.debug("[UPDATE] Old file deleted from OSS: {}", objectName);
                }
            } catch (Exception e) {
                logger.warn("[UPDATE] Failed to delete old file from OSS: {}", e.getMessage());
            }
        }
        String fileExtension = getFileExtension(newFile.getOriginalFilename());
        String uniqueFileName = uploadPath + UUID.randomUUID().toString() + fileExtension;
        String fileUrl = ossService.uploadFile(uniqueFileName, newFile.getInputStream());
        urlSetter.accept(fileUrl);
        logger.debug("[UPDATE] New file uploaded to OSS at: {}", fileUrl);
    }


    @Transactional
    @Override
    @CachePut(value = "mechanics", key = "#id")
    @CacheEvict(value = {"allMechanics", "mechanicsByGarage"}, allEntries = true)
    public MechanicResponseDTO updateMechanic(
            Long id,
            MechanicRequestDTO mechanicRequestDTO,
            MultipartFile profilePic,
            MultipartFile nationalIDPic,
            MultipartFile professionalCertfificate,
            MultipartFile anyRelevantCertificate,
            MultipartFile policeClearanceCertficate) {

        logger.info("Updating mechanic with ID: {}", id);

        Mechanic mechanic = mechanicRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Mechanic not found with ID: {}", id);
                    return new ResourceNotFoundException("Mechanic not found");
                });

        mapper.updateEntityFromDTO(mechanicRequestDTO, mechanic);

        if (mechanicRequestDTO.getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarageId())
                    .orElseThrow(() -> {
                        logger.error("Garage with ID {} not found during update", mechanicRequestDTO.getGarageId());
                        return new ResourceNotFoundException("Garage not found");
                    });
            mechanic.setGarage(garage);
            logger.debug("Updated mechanic's garage to: {}", garage.getBusinessName());
        }

        try {
            Long userId = mechanic.getUser().getId();
            String baseFolder = "MechanicFiles/" + userId + "/";

            if (profilePic != null && !profilePic.isEmpty()) {
                updateSingleFile(profilePic, mechanic.getProfilePic(), mechanic::setProfilePic, baseFolder + "profilePic/");
            }
            if (nationalIDPic != null && !nationalIDPic.isEmpty()) {
                updateSingleFile(nationalIDPic, mechanic.getNationalIDPic(), mechanic::setNationalIDPic, baseFolder + "nationalID/");
            }
            if (professionalCertfificate != null && !professionalCertfificate.isEmpty()) {
                updateSingleFile(professionalCertfificate, mechanic.getProfessionalCertfificate(), mechanic::setProfessionalCertfificate, baseFolder + "profCert/");
            }
            if (anyRelevantCertificate != null && !anyRelevantCertificate.isEmpty()) {
                updateSingleFile(anyRelevantCertificate, mechanic.getAnyRelevantCertificate(), mechanic::setAnyRelevantCertificate, baseFolder + "relevantCert/");
            }
            if (policeClearanceCertficate != null && !policeClearanceCertficate.isEmpty()) {
                updateSingleFile(policeClearanceCertficate, mechanic.getPoliceClearanceCertficate(), mechanic::setPoliceClearanceCertficate, baseFolder + "policeClearance/");
            }
        } catch (IOException e) {
            logger.error("Error reading uploaded file(s) during update: {}", e.getMessage());
            throw new BadRequestException("Error reading uploaded file(s)");
        }

        Mechanic updatedMechanic = mechanicRepository.save(mechanic);
        logger.info("Mechanic with ID {} updated successfully", id);
        return mapper.toResponseDTO(updatedMechanic);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"mechanics", "allMechanics", "mechanicsByGarage"}, allEntries = true)
    public String deleteMechanic(Long id) {
        logger.info("Deleting mechanic with ID: {}", id);

        if (!mechanicRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent mechanic with ID: {}", id);
            throw new ResourceNotFoundException("Mechanic not found");
        }

        mechanicRepository.deleteById(id);
        logger.info("Mechanic with ID {} deleted successfully", id);
        return "Mechanic deleted";
    }

    @Override
    public Optional<String> getMechanicFilesUrlByNationalId(Integer nationalId, int expiryMinutes) {
        logger.info("[OSS URL] Generating presigned URL for National ID Picture for mechanic with ID={}", nationalId);

        return mechanicRepository.findMechanicByNationalIdNumber(nationalId)
                .map(mechanic -> {
                    String savedUrl = mechanic.getNationalIDPic();

                    if (savedUrl == null || savedUrl.isBlank()) {
                        logger.warn("[OSS URL] No National ID Pic URL found for mechanic national ID={}", nationalId);
                        return null;
                    }
                    String objectKey = getObjectNameFromUrl(savedUrl, ossService.getBucketName(), ossService.getEndpoint());

                    if (objectKey == null) {
                        logger.error("[OSS URL] Failed to parse object key from saved URL: {}", savedUrl);
                        return null;
                    }
                    String presignedUrl = ossService.generatePresignedUrl(objectKey, expiryMinutes);

                    logger.debug("[OSS URL] Generated URL for {} will expire in {} minutes", objectKey, expiryMinutes);
                    return presignedUrl;
                });
    }
}
