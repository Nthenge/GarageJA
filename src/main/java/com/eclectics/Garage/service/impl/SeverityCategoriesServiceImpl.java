package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.SeverityCategories;
import com.eclectics.Garage.repository.SeverityCategoryRepository;
import com.eclectics.Garage.service.SeverityCategoriesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeverityCategoriesServiceImpl implements SeverityCategoriesService {

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
        return severityCategoryRepository.save(severityCategories);
    }

    @Override
    @Cacheable(value = "allSeverityCategories")
    public List<SeverityCategories> getAllSeverityCategories() {
        return severityCategoryRepository.findAll();
    }

    @Override
    @Cacheable(value = "severityCategoryByName", key = "#severityName")
    public SeverityCategories getSeverityCategoryByName(String severityName) {
        return severityCategoryRepository.findBySeverityName(severityName)
                .orElseThrow(() -> new RuntimeException("Severity category not found"));
    }
}
