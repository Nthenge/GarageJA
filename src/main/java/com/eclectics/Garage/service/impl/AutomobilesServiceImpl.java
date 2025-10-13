package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.AutoMobileResponseDTO;
import com.eclectics.Garage.dto.AutomobileRequestsDTO;
import com.eclectics.Garage.mapper.AutoMobileMapper;
import com.eclectics.Garage.model.AutoMobiles;
import com.eclectics.Garage.repository.AutomobilesRepository;
import com.eclectics.Garage.service.AutomobilesService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomobilesServiceImpl implements AutomobilesService {

    private final AutomobilesRepository automobilesRepository;
    private final AutoMobileMapper mapper;

    public AutomobilesServiceImpl(AutomobilesRepository automobilesRepository, AutoMobileMapper mapper) {
        this.automobilesRepository = automobilesRepository;
        this.mapper = mapper;
    }

    @Override
    public AutoMobileResponseDTO createAutoMobile(AutomobileRequestsDTO automobileRequestsDTO) {
        AutoMobiles autoMobiles = mapper.toEntity(automobileRequestsDTO);
        AutoMobiles autoMobilesSaved =automobilesRepository.save(autoMobiles);
        return mapper.toResponseDTO(autoMobilesSaved);
    }

    @Override
    public List<AutoMobileResponseDTO> getAllAutomobiles() {
        List<AutoMobiles> autoMobiles = automobilesRepository.findAll();
        return mapper.toResponseDTOList(autoMobiles);
    }

    @Override
    public AutoMobileResponseDTO updateAutoMobile(Long id, AutomobileRequestsDTO automobileRequestsDTO) {
        AutoMobiles existing = automobilesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Automobile not found with id: " + id));

        mapper.updateEntityFromDTO(automobileRequestsDTO, existing);
        existing.setMake(automobileRequestsDTO.getMake());
        existing.setYear(automobileRequestsDTO.getYear());
        existing.setEngineType(automobileRequestsDTO.getEngineType());
        existing.setTransmission(automobileRequestsDTO.getTransmission());

        AutoMobiles saveAutoMobiles = automobilesRepository.save(existing);
        return mapper.toResponseDTO(saveAutoMobiles);
    }

    public List<String> getAllMakes() {
        List<String> autoMobileMakes = automobilesRepository.findAllMakes();
        return autoMobileMakes;
    }
    public List<String> findAllYears() {
        List<String> autoMobilesYears = automobilesRepository.findAllYears();
        return autoMobilesYears;
    }
    public List<String> findAllEngineType() {
        List<String> autoMobilesEngineType = automobilesRepository.findAllEngineType();
        return autoMobilesEngineType;
    }
    public List<String> findAllTransmission() {
        List<String> autoMobilesTransmission = automobilesRepository.findAllTransmission();
        return autoMobilesTransmission;
    }

    @Override
    public void deleteAutoMobile(Long id) {
        if (!automobilesRepository.existsById(id)){
            throw new ResourceNotFoundException("Auto mobile with this id does not exist");
        }
        automobilesRepository.deleteById(id);
    }
}
