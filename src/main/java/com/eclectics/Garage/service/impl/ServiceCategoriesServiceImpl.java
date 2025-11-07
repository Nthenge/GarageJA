package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.ServiceCategoriesResponseDTO;
import com.eclectics.Garage.dto.ServiceCategoriestRequestDTO;
import com.eclectics.Garage.mapper.ServiceCategoriesMapper;
import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.repository.ServiceCategoryRepository;
import com.eclectics.Garage.service.ServiceCategoriesService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ServiceCategoriesServiceImpl implements ServiceCategoriesService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoriesServiceImpl.class);

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceCategoriesMapper mapper;

    private final Map<String, ServiceCategories> categoryCache = new ConcurrentHashMap<>();

    private final Set<String> categoryNames = new HashSet<>();

    public ServiceCategoriesServiceImpl(ServiceCategoryRepository serviceCategoryRepository, ServiceCategoriesMapper mapper) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.mapper = mapper;
        preloadCategories();
    }

    private void preloadCategories() {
        logger.info("Preloading service categories into memory...");
        List<ServiceCategories> allCategories = serviceCategoryRepository.findAll();
        for (ServiceCategories c : allCategories) {
            categoryCache.put(c.getServiceCategoryName().toLowerCase(), c);
            categoryNames.add(c.getServiceCategoryName().toLowerCase());
        }
        logger.info("Preloaded {} service categories into cache", categoryCache.size());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceCategories", allEntries = true),
            @CacheEvict(value = "serviceCategoryByName", allEntries = true)
    })
    public ServiceCategories createCategory(ServiceCategoriestRequestDTO serviceCategoriestRequestDTO) {
        String categoryName = serviceCategoriestRequestDTO.getServiceCategoryName().toLowerCase();

        if (categoryNames.contains(categoryName)) {
            logger.warn("Attempted to create duplicate category: {}", categoryName);
            throw new IllegalArgumentException("Category already exists!");
        }

        ServiceCategories serviceCategories = mapper.toEntity(serviceCategoriestRequestDTO);
        ServiceCategories savedCategory = serviceCategoryRepository.save(serviceCategories);

        categoryCache.put(categoryName, savedCategory);
        categoryNames.add(categoryName);

        logger.info("Service category created successfully with ID: {}", savedCategory.getId());
        return savedCategory;
    }

    @Override
    @Cacheable(value = "allServiceCategories")
    public List<ServiceCategoriesResponseDTO> getAllServiceCategories() {
        logger.info("Fetching all service categories (cached size: {})", categoryCache.size());

        if (!categoryCache.isEmpty()) {
            return categoryCache.values().stream()
                    .map(mapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        List<ServiceCategories> categories = serviceCategoryRepository.findAll();
        categories.forEach(c -> categoryCache.put(c.getServiceCategoryName().toLowerCase(), c));
        return mapper.toResponseDTOList(categories);
    }

    @Override
    @Cacheable(value = "serviceCategoryByName", key = "#serviceCategoryName")
    public ServiceCategoriesResponseDTO getServiceCategoryByName(String serviceCategoryName) {
        String nameKey = serviceCategoryName.toLowerCase();
        logger.info("Fetching service category by name: {}", serviceCategoryName);

        if (categoryCache.containsKey(nameKey)) {
            logger.debug("Found category in cache: {}", serviceCategoryName);
            return mapper.toResponseDTO(categoryCache.get(nameKey));
        }

        return serviceCategoryRepository.serviceCategoryName(serviceCategoryName)
                .map(category -> {
                    logger.debug("Service category found in DB: {}", category.getServiceCategoryName());
                    categoryCache.put(nameKey, category);
                    categoryNames.add(nameKey);
                    return mapper.toResponseDTO(category);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceCategories", allEntries = true),
            @CacheEvict(value = "serviceCategoryByName", allEntries = true)
    })
    public void delete(Long id) {
        logger.warn("Deleting service category with ID: {}", id);

        Optional<ServiceCategories> optional = serviceCategoryRepository.findById(id);
        if (optional.isPresent()) {
            String nameKey = optional.get().getServiceCategoryName().toLowerCase();
            categoryCache.remove(nameKey);
            categoryNames.remove(nameKey);
        }

        serviceCategoryRepository.deleteById(id);
        logger.info("Service category with ID {} deleted successfully", id);
    }
}
