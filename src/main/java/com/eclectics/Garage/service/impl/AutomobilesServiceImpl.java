package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.AutoMobileResponseDTO;
import com.eclectics.Garage.dto.AutomobileRequestsDTO;
import com.eclectics.Garage.mapper.AutoMobileMapper;
import com.eclectics.Garage.model.AutoMobiles;
import com.eclectics.Garage.repository.AutomobilesRepository;
import com.eclectics.Garage.service.AutomobilesService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomobilesServiceImpl implements AutomobilesService {

    private static final Logger logger = LoggerFactory.getLogger(AutomobilesServiceImpl.class);

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
        logger.info("[CREATE] Creating automobile with make={}, year={}, engineType={}, transmission={}",
                automobileRequestsDTO.getMake(), automobileRequestsDTO.getYear(),
                automobileRequestsDTO.getEngineType(), automobileRequestsDTO.getTransmission());

        AutoMobiles autoMobiles = mapper.toEntity(automobileRequestsDTO);
        AutoMobiles saved = automobilesRepository.save(autoMobiles);

        logger.info("[CREATE SUCCESS] Automobile created with ID={} and make={}", saved.getId(), saved.getMake());
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Cacheable(value = "allAutomobiles")
    public List<AutoMobileResponseDTO> getAllAutomobiles() {
        logger.info("[FETCH ALL] Retrieving all automobiles from cache or DB");
        List<AutoMobiles> autoMobiles = automobilesRepository.findAll();
        logger.debug("[FETCH ALL] Total automobiles fetched: {}", autoMobiles.size());
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
        logger.info("[UPDATE] Attempting to update automobile with ID={}", id);

        AutoMobiles existing = automobilesRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("[UPDATE FAILED] Automobile not found with ID={}", id);
                    return new ResourceNotFoundException("Automobile not found with id: " + id);
                });

        mapper.updateEntityFromDTO(automobileRequestsDTO, existing);
        existing.setMake(automobileRequestsDTO.getMake());
        existing.setYear(automobileRequestsDTO.getYear());
        existing.setEngineType(automobileRequestsDTO.getEngineType());
        existing.setTransmission(automobileRequestsDTO.getTransmission());

        AutoMobiles updated = automobilesRepository.save(existing);
        logger.info("[UPDATE SUCCESS] Automobile updated with ID={} → make={}, year={}",
                updated.getId(), updated.getMake(), updated.getYear());

        return mapper.toResponseDTO(updated);
    }

    @Override
    @Cacheable(value = "autoMobileMakes")
    public List<String> getAllMakes() {
        logger.info("[FETCH] Retrieving all automobile makes");
        List<String> makes = automobilesRepository.findAllMakes();
        logger.debug("[FETCH] Total makes fetched: {}", makes.size());
        return makes;
    }

    @Override
    @Cacheable(value = "autoMobileYears")
    public List<String> findAllYears() {
        logger.info("[FETCH] Retrieving all automobile years");
        List<String> years = automobilesRepository.findAllYears();
        logger.debug("[FETCH] Total years fetched: {}", years.size());
        return years;
    }

    @Override
    @Cacheable(value = "autoMobileEngineTypes")
    public List<String> findAllEngineType() {
        logger.info("[FETCH] Retrieving all automobile engine types");
        List<String> engineTypes = automobilesRepository.findAllEngineType();
        logger.debug("[FETCH] Total engine types fetched: {}", engineTypes.size());
        return engineTypes;
    }

    @Override
    @Cacheable(value = "autoMobileTransmissions")
    public List<String> findAllTransmission() {
        logger.info("[FETCH] Retrieving all automobile transmissions");
        List<String> transmissions = automobilesRepository.findAllTransmission();
        logger.debug("[FETCH] Total transmissions fetched: {}", transmissions.size());
        return transmissions;
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
        logger.info("[DELETE] Attempting to delete automobile with ID={}", id);

        if (!automobilesRepository.existsById(id)) {
            logger.warn("[DELETE FAILED] Automobile not found with ID={}", id);
            throw new ResourceNotFoundException("Automobile with this id does not exist");
        }

        automobilesRepository.deleteById(id);
        logger.info("[DELETE SUCCESS] Automobile deleted with ID={}", id);
    }
}
