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
import java.util.function.Consumer;

@Service
public class MechanicServiceImpl implements MechanicService {

    private static final Logger logger = LoggerFactory.getLogger(MechanicServiceImpl.class);

    private final MechanicRepository mechanicRepository;
    private final GarageRepository garageRepository;
    private final AuthenticationService authenticationService;
    private final MechanicMapper mapper;

    public MechanicServiceImpl(MechanicRepository mechanicRepository, GarageRepository garageRepository,
                               AuthenticationService authenticationService, MechanicMapper mapper) {
        this.mechanicRepository = mechanicRepository;
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
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

    private void setFileIfPresent(MultipartFile file, Consumer<byte[]> setter) throws IOException {
        if (file != null && !file.isEmpty()) {
            logger.debug("Processing uploaded file: {}", file.getOriginalFilename());
            setter.accept(file.getBytes());
        }
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

        setFileIfPresent(profilePic, mechanic::setProfilePic);
        setFileIfPresent(nationalIDPic, mechanic::setNationalIDPic);
        setFileIfPresent(professionalCertfificate, mechanic::setProfessionalCertfificate);
        setFileIfPresent(anyRelevantCertificate, mechanic::setAnyRelevantCertificate);
        setFileIfPresent(policeClearanceCertficate, mechanic::setPoliceClearanceCertficate);

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
            setFileIfPresent(profilePic, mechanic::setProfilePic);
            setFileIfPresent(nationalIDPic, mechanic::setNationalIDPic);
            setFileIfPresent(professionalCertfificate, mechanic::setProfessionalCertfificate);
            setFileIfPresent(anyRelevantCertificate, mechanic::setAnyRelevantCertificate);
            setFileIfPresent(policeClearanceCertficate, mechanic::setPoliceClearanceCertficate);
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
}
