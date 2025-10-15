package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.repository.ServiceCategoryRepository;
import com.eclectics.Garage.service.ServiceCategoriesService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoriesServiceImpl implements ServiceCategoriesService {

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
        return serviceCategoryRepository.save(serviceCategories);
    }

    @Override
    @Cacheable(value = "allServiceCategories")
    public List<ServiceCategories> getAllServiceCategories() {
        return serviceCategoryRepository.findAll();
    }

    @Override
    @Cacheable(value = "serviceCategoryByName", key = "#serviceCategoryName")
    public ServiceCategories getServiceCategoryByName(String serviceCategoryName) {
        return serviceCategoryRepository.serviceCategoryName(serviceCategoryName)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceCategories", allEntries = true),
            @CacheEvict(value = "serviceCategoryByName", allEntries = true)
    })
    public void delete(Long id) {
        serviceCategoryRepository.deleteById(id);
    }
}
