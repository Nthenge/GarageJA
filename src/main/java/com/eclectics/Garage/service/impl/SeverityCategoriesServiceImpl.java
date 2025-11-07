package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.SeverityRequestDTO;
import com.eclectics.Garage.dto.SeverityResponseDTO;
import com.eclectics.Garage.mapper.SeverityMapper;
import com.eclectics.Garage.model.SeverityCategories;
import com.eclectics.Garage.repository.SeverityCategoryRepository;
import com.eclectics.Garage.service.SeverityCategoriesService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SeverityCategoriesServiceImpl implements SeverityCategoriesService {

    private static final Logger logger = LoggerFactory.getLogger(SeverityCategoriesServiceImpl.class);

    private final SeverityCategoryRepository severityCategoryRepository;
    private final SeverityMapper mapper;

    private final Map<Long, SeverityCategories> categoryCache = new ConcurrentHashMap<>();
    private final Map<String, SeverityCategories> nameToCategoryMap = new ConcurrentHashMap<>();
    private final List<SeverityCategories> categoryList = Collections.synchronizedList(new ArrayList<>());

    public SeverityCategoriesServiceImpl(SeverityCategoryRepository severityCategoryRepository, SeverityMapper mapper) {
        this.severityCategoryRepository = severityCategoryRepository;
        this.mapper = mapper;

        preloadCache();
    }

    private void preloadCache() {
        List<SeverityCategories> all = severityCategoryRepository.findAll();
        for (SeverityCategories c : all) {
            categoryCache.put(c.getId(), c);
            nameToCategoryMap.put(c.getSeverityName().toLowerCase(), c);
            categoryList.add(c);
        }
        logger.info("Preloaded {} severity categories into cache.", all.size());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allSeverityCategories", allEntries = true),
            @CacheEvict(value = "severityCategoryByName", allEntries = true)
    })
    public SeverityCategories createCategory(SeverityRequestDTO severityRequestDTO) {
        logger.info("Attempting to create a new severity category: {}", severityRequestDTO.getSeverityName());

        String nameKey = severityRequestDTO.getSeverityName().toLowerCase();

        if (nameToCategoryMap.containsKey(nameKey)) {
            logger.warn("Severity category '{}' already exists in memory cache. Skipping creation.", nameKey);
            return nameToCategoryMap.get(nameKey);
        }

        SeverityCategories severityCategories = mapper.toEntity(severityRequestDTO);
        SeverityCategories saved = severityCategoryRepository.save(severityCategories);

        categoryCache.put(saved.getId(), saved);
        nameToCategoryMap.put(nameKey, saved);
        categoryList.add(saved);

        logger.info("Successfully created severity category with ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Cacheable(value = "allSeverityCategories")
    public List<SeverityResponseDTO> getAllSeverityCategories() {
        logger.info("Fetching all severity categories...");

        if (!categoryList.isEmpty()) {
            logger.debug("Returning severity categories from cache ({} items).", categoryList.size());
            return mapper.toResponseListDTO(new ArrayList<>(categoryList));
        }

        List<SeverityCategories> categories = severityCategoryRepository.findAll();
        logger.info("Fetched {} severity categories from database.", categories.size());

        categories.forEach(cat -> {
            categoryCache.put(cat.getId(), cat);
            nameToCategoryMap.put(cat.getSeverityName().toLowerCase(), cat);
        });
        categoryList.addAll(categories);

        return mapper.toResponseListDTO(categories);
    }

    @Override
    @Cacheable(value = "severityCategoryByName", key = "#severityName")
    public SeverityResponseDTO getSeverityCategoryByName(String severityName) {
        logger.info("Fetching severity category by name: {}", severityName);
        String key = severityName.toLowerCase();

        if (nameToCategoryMap.containsKey(key)) {
            logger.debug("Returning '{}' category from cache.", key);
            return mapper.toResponseDTO(nameToCategoryMap.get(key));
        }

        SeverityCategories category = severityCategoryRepository.findBySeverityName(severityName)
                .orElseThrow(() -> {
                    logger.error("Severity category not found: {}", severityName);
                    return new ResourceNotFoundException("Severity category not found");
                });

        categoryCache.put(category.getId(), category);
        nameToCategoryMap.put(key, category);
        categoryList.add(category);

        logger.info("Added '{}' category to cache after DB lookup.", severityName);
        return mapper.toResponseDTO(category);
    }
}
