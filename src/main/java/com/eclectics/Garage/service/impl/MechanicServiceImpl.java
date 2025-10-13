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

    public MechanicServiceImpl(MechanicRepository mechanicRepository, GarageRepository garageRepository, AuthenticationService authenticationService, MechanicMapper mapper) {
        this.mechanicRepository = mechanicRepository;
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
    }

    public ProfileCompleteDTO checkProfileCompletion(Mechanic mechanic){
        List<String> missingFields = mechanic.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<MechanicResponseDTO> findByUserId(Long id) {
        Optional<Mechanic> mechanic = mechanicRepository.findByUserId(id);
        return mechanic.map(mapper::toResponseDTO);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return mechanicRepository.findByUserId(userId)
                .map(Mechanic::isComplete) // calls the entity method
                .orElse(false);
    }

    private void setFileIfPresent(MultipartFile file, Consumer<byte[]> setter) throws IOException {
        if (file != null && !file.isEmpty()) {
            setter.accept(file.getBytes());
        }
    }


    @Transactional
    @Override
    public MechanicResponseDTO createMechanic(MechanicRequestDTO mechanicRequestDTO, MultipartFile profilePic, MultipartFile nationalIDPic, MultipartFile professionalCertfificate, MultipartFile anyRelevantCertificate, MultipartFile policeClearanceCertficate) throws java.io.IOException {

        Mechanic mechanic = mapper.toEntity(mechanicRequestDTO);

        Optional<Mechanic> mechanicExist = mechanicRepository.findMechanicByNationalIdNumber(mechanic.getNationalIdNumber());
        if (mechanicExist.isPresent()) {
            throw new ResourceNotFoundException("Mechanic with this national ID already exist");
        }

        User userid = authenticationService.getCurrentUser();
        mechanic.setUser(userid);

        if (mechanicRequestDTO.getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garage with this id " + mechanicRequestDTO.getGarageId() + " not found"));
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
    public Optional<MechanicResponseDTO> getMechanicByNationalId(Integer nationalIdNumber) {
        return mechanicRepository.findMechanicByNationalIdNumber(nationalIdNumber)
                .map(mapper::toResponseDTO);
    }

    //For system admin
    @Transactional(readOnly = true)
    @Override
    public List<MechanicResponseDTO> getAllMechanics() {
        List<Mechanic> mechanics = mechanicRepository.findAll();
        return mapper.toResponseDTOList(mechanics);
    }

    //Get all mechanics for a certain garage
    @Transactional(readOnly = true)
    @Override
    public List<MechanicResponseDTO> getMechanicsByGarageId(Long garageId) {
        List<Mechanic> garageMechanics= mechanicRepository.findByGarageId(garageId);
        return mapper.toResponseDTOList(garageMechanics);
    }

    @Transactional
    @Override
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
            if (profilePic != null && !profilePic.isEmpty()) {
                mechanic.setProfilePic(profilePic.getBytes());
            }
            if (nationalIDPic != null && !nationalIDPic.isEmpty()) {
                mechanic.setNationalIDPic(nationalIDPic.getBytes());
            }
            if (professionalCertfificate != null && !professionalCertfificate.isEmpty()) {
                mechanic.setProfessionalCertfificate(professionalCertfificate.getBytes());
            }
            if (anyRelevantCertificate != null && !anyRelevantCertificate.isEmpty()) {
                mechanic.setAnyRelevantCertificate(anyRelevantCertificate.getBytes());
            }
            if (policeClearanceCertficate != null && !policeClearanceCertficate.isEmpty()) {
                mechanic.setPoliceClearanceCertficate(policeClearanceCertficate.getBytes());
            }
        } catch (IOException e) {
            throw new BadRequestException("Error reading uploaded file(s)");
        }

        Mechanic savedMechanic = mechanicRepository.save(mechanic);

        return mapper.toResponseDTO(savedMechanic);
    }


    @Transactional
    @Override
    public String deleteMechanic(Long id) {
        mechanicRepository.deleteById(id);
        return "Mechanic deleted";
    }
}
