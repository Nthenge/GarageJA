package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.AutoMobileResponseDTO;
import com.eclectics.Garage.dto.AutomobileRequestsDTO;
import com.eclectics.Garage.mapper.AutoMobileMapper;
import com.eclectics.Garage.model.AutoMobiles;
import com.eclectics.Garage.repository.AutomobilesRepository;
import com.eclectics.Garage.service.AutomobilesService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Caching(evict = {
            @CacheEvict(value = "allAutomobiles", allEntries = true),
            @CacheEvict(value = "autoMobileMakes", allEntries = true),
            @CacheEvict(value = "autoMobileYears", allEntries = true),
            @CacheEvict(value = "autoMobileEngineTypes", allEntries = true),
            @CacheEvict(value = "autoMobileTransmissions", allEntries = true)
    })
    public AutoMobileResponseDTO createAutoMobile(AutomobileRequestsDTO automobileRequestsDTO) {
        AutoMobiles autoMobiles = mapper.toEntity(automobileRequestsDTO);
        AutoMobiles saved = automobilesRepository.save(autoMobiles);
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Cacheable(value = "allAutomobiles")
    public List<AutoMobileResponseDTO> getAllAutomobiles() {
        List<AutoMobiles> autoMobiles = automobilesRepository.findAll();
        return mapper.toResponseDTOList(autoMobiles);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allAutomobiles", allEntries = true),
            @CacheEvict(value = "autoMobileMakes", allEntries = true),
            @CacheEvict(value = "autoMobileYears", allEntries = true),
            @CacheEvict(value = "autoMobileEngineTypes", allEntries = true),
            @CacheEvict(value = "autoMobileTransmissions", allEntries = true)
    })
    public AutoMobileResponseDTO updateAutoMobile(Long id, AutomobileRequestsDTO automobileRequestsDTO) {
        AutoMobiles existing = automobilesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Automobile not found with id: " + id));

        mapper.updateEntityFromDTO(automobileRequestsDTO, existing);
        existing.setMake(automobileRequestsDTO.getMake());
        existing.setYear(automobileRequestsDTO.getYear());
        existing.setEngineType(automobileRequestsDTO.getEngineType());
        existing.setTransmission(automobileRequestsDTO.getTransmission());

        AutoMobiles updated = automobilesRepository.save(existing);
        return mapper.toResponseDTO(updated);
    }

    @Override
    @Cacheable(value = "autoMobileMakes")
    public List<String> getAllMakes() {
        return automobilesRepository.findAllMakes();
    }

    @Override
    @Cacheable(value = "autoMobileYears")
    public List<String> findAllYears() {
        return automobilesRepository.findAllYears();
    }

    @Override
    @Cacheable(value = "autoMobileEngineTypes")
    public List<String> findAllEngineType() {
        return automobilesRepository.findAllEngineType();
    }

    @Override
    @Cacheable(value = "autoMobileTransmissions")
    public List<String> findAllTransmission() {
        return automobilesRepository.findAllTransmission();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allAutomobiles", allEntries = true),
            @CacheEvict(value = "autoMobileMakes", allEntries = true),
            @CacheEvict(value = "autoMobileYears", allEntries = true),
            @CacheEvict(value = "autoMobileEngineTypes", allEntries = true),
            @CacheEvict(value = "autoMobileTransmissions", allEntries = true)
    })
    public void deleteAutoMobile(Long id) {
        if (!automobilesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Auto mobile with this id does not exist");
        }
        automobilesRepository.deleteById(id);
    }
}
