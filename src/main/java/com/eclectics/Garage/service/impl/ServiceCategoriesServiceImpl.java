package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.repository.ServiceCategoryRepository;
import com.eclectics.Garage.service.ServiceCategoriesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoriesServiceImpl implements ServiceCategoriesService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoriesServiceImpl.class);

    private final ServiceCategoryRepository serviceCategoryRepository;

    public ServiceCategoriesServiceImpl(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceCategories", allEntries = true),
            @CacheEvict(value = "serviceCategoryByName", allEntries = true)
    })
    public ServiceCategories createCategory(ServiceCategories serviceCategories) {
        logger.info("Creating new service category: {}", serviceCategories.getServiceCategoryName());
        ServiceCategories savedCategory = serviceCategoryRepository.save(serviceCategories);
        logger.info("Service category created successfully with ID: {}", savedCategory.getId());
        return savedCategory;
    }

    @Override
    @Cacheable(value = "allServiceCategories")
    public List<ServiceCategories> getAllServiceCategories() {
        logger.info("Fetching all service categories");
        List<ServiceCategories> categories = serviceCategoryRepository.findAll();
        logger.debug("Total service categories found: {}", categories.size());
        return categories;
    }

    @Override
    @Cacheable(value = "serviceCategoryByName", key = "#serviceCategoryName")
    public ServiceCategories getServiceCategoryByName(String serviceCategoryName) {
        logger.info("Fetching service category by name: {}", serviceCategoryName);
        return serviceCategoryRepository.serviceCategoryName(serviceCategoryName)
                .map(category -> {
                    logger.debug("Service category found: {}", category.getServiceCategoryName());
                    return category;
                })
                .orElseThrow(() -> {
                    logger.error("Service category with name '{}' not found", serviceCategoryName);
                    return new RuntimeException("Category not found");
                });
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceCategories", allEntries = true),
            @CacheEvict(value = "serviceCategoryByName", allEntries = true)
    })
    public void delete(Long id) {
        logger.warn("Deleting service category with ID: {}", id);
        serviceCategoryRepository.deleteById(id);
        logger.info("Service category with ID {} deleted successfully", id);
    }
}
