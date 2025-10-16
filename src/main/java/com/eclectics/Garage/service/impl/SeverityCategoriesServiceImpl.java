package com.eclectics.Garage.service.impl;

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

import java.util.List;

@Service
public class SeverityCategoriesServiceImpl implements SeverityCategoriesService {

    private static final Logger logger = LoggerFactory.getLogger(SeverityCategoriesServiceImpl.class);

    private final SeverityCategoryRepository severityCategoryRepository;

    public SeverityCategoriesServiceImpl(SeverityCategoryRepository severityCategoryRepository) {
        this.severityCategoryRepository = severityCategoryRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allSeverityCategories", allEntries = true),
            @CacheEvict(value = "severityCategoryByName", allEntries = true)
    })
    public SeverityCategories createCategory(SeverityCategories severityCategories) {
        logger.info("Attempting to create a new severity category: {}", severityCategories.getSeverityName());
        SeverityCategories saved = severityCategoryRepository.save(severityCategories);
        logger.info("Successfully created severity category with ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Cacheable(value = "allSeverityCategories")
    public List<SeverityCategories> getAllSeverityCategories() {
        logger.info("Fetching all severity categories from database (may be cached on subsequent calls)...");
        List<SeverityCategories> categories = severityCategoryRepository.findAll();
        logger.info("Fetched {} severity categories.", categories.size());
        return categories;
    }

    @Override
    @Cacheable(value = "severityCategoryByName", key = "#severityName")
    public SeverityCategories getSeverityCategoryByName(String severityName) {
        logger.info("Fetching severity category by name: {}", severityName);
        return severityCategoryRepository.findBySeverityName(severityName)
                .map(category -> {
                    logger.info("Found severity category: {}", category.getSeverityName());
                    return category;
                })
                .orElseThrow(() -> {
                    logger.error("Severity category not found: {}", severityName);
                    return new ResourceNotFoundException("Severity category not found");
                });
    }
}
