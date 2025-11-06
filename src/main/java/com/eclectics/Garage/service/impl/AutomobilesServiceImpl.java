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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AutomobilesServiceImpl implements AutomobilesService {

    private static final Logger logger = LoggerFactory.getLogger(AutomobilesServiceImpl.class);

    private final AutomobilesRepository automobilesRepository;
    private final AutoMobileMapper mapper;

    private final Map<Long, AutoMobiles> automobileCache = new ConcurrentHashMap<>();
    private final Map<String, List<AutoMobiles>> makeToCars = new ConcurrentHashMap<>();
    private final Set<String> engineTypes = new HashSet<>();
    private final TreeSet<String> years = new TreeSet<>();
    private final Set<String> transmissions = new HashSet<>();

    public AutomobilesServiceImpl(AutomobilesRepository automobilesRepository, AutoMobileMapper mapper) {
        this.automobilesRepository = automobilesRepository;
        this.mapper = mapper;
        preloadAutomobiles();
    }

    private void preloadAutomobiles() {
        logger.info("Preloading automobiles into in-memory cache...");
        List<AutoMobiles> all = automobilesRepository.findAll();
        for (AutoMobiles car : all) {
            automobileCache.put(car.getId(), car);
            makeToCars.computeIfAbsent(car.getMake().toLowerCase(), k -> new ArrayList<>()).add(car);
            engineTypes.add(car.getEngineType());
            years.add(car.getYear());
            transmissions.add(car.getTransmission());
        }
        logger.info("Preloaded {} automobiles into memory cache", automobileCache.size());
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

        automobileCache.put(saved.getId(), saved);
        makeToCars.computeIfAbsent(saved.getMake().toLowerCase(), k -> new ArrayList<>()).add(saved);
        engineTypes.add(saved.getEngineType());
        years.add(saved.getYear());
        transmissions.add(saved.getTransmission());

        logger.info("[CREATE SUCCESS] Automobile created with ID={} and make={}", saved.getId(), saved.getMake());
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Cacheable(value = "allAutomobiles")
    public List<AutoMobileResponseDTO> getAllAutomobiles() {
        logger.info("[FETCH ALL] Retrieving all automobiles (in-memory size={})", automobileCache.size());

        if (!automobileCache.isEmpty()) {
            return automobileCache.values().stream()
                    .map(mapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        List<AutoMobiles> autoMobiles = automobilesRepository.findAll();
        autoMobiles.forEach(car -> automobileCache.put(car.getId(), car));
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

        AutoMobiles existing = automobileCache.get(id);
        if (existing == null) {
            existing = automobilesRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Automobile not found with id: " + id));
        }

        mapper.updateEntityFromDTO(automobileRequestsDTO, existing);
        AutoMobiles updated = automobilesRepository.save(existing);

        automobileCache.put(id, updated);
        makeToCars.computeIfAbsent(updated.getMake().toLowerCase(), k -> new ArrayList<>()).add(updated);
        engineTypes.add(updated.getEngineType());
        years.add(updated.getYear());
        transmissions.add(updated.getTransmission());

        logger.info("[UPDATE SUCCESS] Automobile updated with ID={} → make={}, year={}",
                updated.getId(), updated.getMake(), updated.getYear());

        return mapper.toResponseDTO(updated);
    }

    @Override
    @Cacheable(value = "autoMobileMakes")
    public List<String> getAllMakes() {
        logger.info("[FETCH] Retrieving all automobile makes (cached)");
        if (!makeToCars.isEmpty()) {
            return new ArrayList<>(makeToCars.keySet());
        }
        List<String> makes = automobilesRepository.findAllMakes();
        makes.forEach(make -> makeToCars.putIfAbsent(make.toLowerCase(), new ArrayList<>()));
        return makes;
    }

    @Override
    @Cacheable(value = "autoMobileYears")
    public List<String> findAllYears() {
        logger.info("[FETCH] Retrieving all automobile years (sorted TreeSet)");
        if (!years.isEmpty()) {
            return new ArrayList<>(years);
        }
        List<String> fetchedYears = automobilesRepository.findAllYears();
        years.addAll(fetchedYears);
        return fetchedYears;
    }

    @Override
    @Cacheable(value = "autoMobileEngineTypes")
    public List<String> findAllEngineType() {
        logger.info("[FETCH] Retrieving all automobile engine types (in-memory)");
        if (!engineTypes.isEmpty()) {
            return new ArrayList<>(engineTypes);
        }
        List<String> fetched = automobilesRepository.findAllEngineType();
        engineTypes.addAll(fetched);
        return fetched;
    }

    @Override
    @Cacheable(value = "autoMobileTransmissions")
    public List<String> findAllTransmission() {
        logger.info("[FETCH] Retrieving all automobile transmissions (in-memory)");
        if (!transmissions.isEmpty()) {
            return new ArrayList<>(transmissions);
        }
        List<String> fetched = automobilesRepository.findAllTransmission();
        transmissions.addAll(fetched);
        return fetched;
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

        AutoMobiles car = automobileCache.get(id);
        if (car == null) {
            throw new ResourceNotFoundException("Automobile not found with ID: " + id);
        }

        automobilesRepository.deleteById(id);

        automobileCache.remove(id);
        makeToCars.getOrDefault(car.getMake().toLowerCase(), new ArrayList<>()).remove(car);
        engineTypes.remove(car.getEngineType());
        years.remove(car.getYear());
        transmissions.remove(car.getTransmission());

        logger.info("[DELETE SUCCESS] Automobile deleted with ID={}", id);
    }
}
