package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.mapper.MechanicMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.MechanicService;
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
    private final UsersRepository usersRepository;
    private final AuthenticationService authenticationService;
    private final MechanicMapper mapper;

    public MechanicServiceImpl(MechanicRepository mechanicRepository, GarageRepository garageRepository, UsersRepository usersRepository, AuthenticationService authenticationService, MechanicMapper mapper) {
        this.mechanicRepository = mechanicRepository;
        this.garageRepository = garageRepository;
        this.usersRepository = usersRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
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
    public MechanicResponseDTO createMechanic(MechanicRequestDTO mechanicRequestDTO, MultipartFile profilepic, MultipartFile nationalIdFile, MultipartFile profCert, MultipartFile anyRelCert, MultipartFile polCleCert) throws java.io.IOException {

        Mechanic mechanic = mapper.toEntity(mechanicRequestDTO);

        Optional<Mechanic> mechanicExist = mechanicRepository.findMechanicByNationalIdNumber(mechanic.getNationalIdNumber());
        if (mechanicExist.isPresent()) {
            throw new RuntimeException("Mechanic with this national ID already exist");
        }

        User userid = authenticationService.getCurrentUser();
        mechanic.setUser(userid);

        if (mechanic.getGarage() != null && mechanic.getGarage().getGarageId() != null) {
            Long garageAdminId = mechanic.getGarage().getGarageId();
            Garage garage = garageRepository.findByGarageId(garageAdminId)
                    .orElseThrow(() -> new RuntimeException("Garage with this id " + garageAdminId + " not found"));

            mechanic.setGarage(garage);
        }

        setFileIfPresent(profilepic, mechanic::setProfilePic);
        setFileIfPresent(nationalIdFile, mechanic::setNationalIDPic);
        setFileIfPresent(profCert, mechanic::setProfessionalCertfificate);
        setFileIfPresent(anyRelCert, mechanic::setAnyRelevantCertificate);
        setFileIfPresent(polCleCert, mechanic::setPoliceClearanceCertficate);

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
    public MechanicResponseDTO updateMechanic(Long id, MechanicRequestDTO mechanicRequestDTO) {
        Mechanic mechanic = mechanicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));

        mapper.updateEntityFromDTO(mechanicRequestDTO, mechanic);

        if (mechanicRequestDTO.getGarage() != null && mechanicRequestDTO.getGarage().getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarage().getGarageId())
                    .orElseThrow(() -> new RuntimeException("Garage not found"));
            mechanic.setGarage(garage);
        }

        Mechanic saved = mechanicRepository.save(mechanic);
        return mapper.toResponseDTO(saved);
    }

    @Transactional
    @Override
    public String deleteMechanic(Long id) {
        mechanicRepository.deleteById(id);
        return "Mechanic deleted";
    }
}
