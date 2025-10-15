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
        List<String> missingFields = mechanic.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByUser", key = "#id")
    public Optional<MechanicResponseDTO> findByUserId(Long id) {
        Optional<Mechanic> mechanic = mechanicRepository.findByUserId(id);
        return mechanic.map(mapper::toResponseDTO);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return mechanicRepository.findByUserId(userId)
                .map(Mechanic::isComplete)
                .orElse(false);
    }

    private void setFileIfPresent(MultipartFile file, Consumer<byte[]> setter) throws IOException {
        if (file != null && !file.isEmpty()) {
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

        Mechanic mechanic = mapper.toEntity(mechanicRequestDTO);

        Optional<Mechanic> mechanicExist = mechanicRepository.findMechanicByNationalIdNumber(mechanic.getNationalIdNumber());
        if (mechanicExist.isPresent()) {
            throw new ResourceNotFoundException("Mechanic with this national ID already exists");
        }

        User userid = authenticationService.getCurrentUser();
        mechanic.setUser(userid);

        if (mechanicRequestDTO.getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garage with id " + mechanicRequestDTO.getGarageId() + " not found"));
            mechanic.setGarage(garage);
        }

        setFileIfPresent(profilePic, mechanic::setProfilePic);
        setFileIfPresent(nationalIDPic, mechanic::setNationalIDPic);
        setFileIfPresent(professionalCertfificate, mechanic::setProfessionalCertfificate);
        setFileIfPresent(anyRelevantCertificate, mechanic::setAnyRelevantCertificate);
        setFileIfPresent(policeClearanceCertficate, mechanic::setPoliceClearanceCertficate);

        Mechanic savedMechanic = mechanicRepository.save(mechanic);
        return mapper.toResponseDTO(savedMechanic);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByNationalId", key = "#nationalIdNumber")
    public Optional<MechanicResponseDTO> getMechanicByNationalId(Integer nationalIdNumber) {
        return mechanicRepository.findMechanicByNationalIdNumber(nationalIdNumber)
                .map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "allMechanics")
    public List<MechanicResponseDTO> getAllMechanics() {
        List<Mechanic> mechanics = mechanicRepository.findAll();
        return mapper.toResponseDTOList(mechanics);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByGarage", key = "#garageId")
    public List<MechanicResponseDTO> getMechanicsByGarageId(Long garageId) {
        List<Mechanic> garageMechanics = mechanicRepository.findByGarageId(garageId);
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

        Mechanic mechanic = mechanicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        mapper.updateEntityFromDTO(mechanicRequestDTO, mechanic);

        if (mechanicRequestDTO.getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));
            mechanic.setGarage(garage);
        }

        try {
            setFileIfPresent(profilePic, mechanic::setProfilePic);
            setFileIfPresent(nationalIDPic, mechanic::setNationalIDPic);
            setFileIfPresent(professionalCertfificate, mechanic::setProfessionalCertfificate);
            setFileIfPresent(anyRelevantCertificate, mechanic::setAnyRelevantCertificate);
            setFileIfPresent(policeClearanceCertficate, mechanic::setPoliceClearanceCertficate);
        } catch (IOException e) {
            throw new BadRequestException("Error reading uploaded file(s)");
        }

        Mechanic savedMechanic = mechanicRepository.save(mechanic);
        return mapper.toResponseDTO(savedMechanic);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"mechanics", "allMechanics", "mechanicsByGarage"}, allEntries = true)
    public String deleteMechanic(Long id) {
        if (!mechanicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mechanic not found");
        }
        mechanicRepository.deleteById(id);
        return "Mechanic deleted";
    }
}
