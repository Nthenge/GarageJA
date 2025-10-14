package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.repository.ServiceCategoryRepository;
import com.eclectics.Garage.service.ServiceCategoriesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoriesServiceImpl implements ServiceCategoriesService {
    private final ServiceCategoryRepository serviceCategoryRepository;


    public ServiceCategoriesServiceImpl(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    public ServiceCategories createCategory(ServiceCategories serviceCategories){
        return serviceCategoryRepository.save(serviceCategories);
    }

    public List<ServiceCategories> getAllServiceCategories(){
        return serviceCategoryRepository.findAll();
    }

    public ServiceCategories getServiceCategoryByName(String serviceCategoryName){
        return serviceCategoryRepository.serviceCategoryName(serviceCategoryName)
                .orElseThrow(()->new RuntimeException("Category not found"));
    }

    @Override
    public void delete(Long id) {
    serviceCategoryRepository.deleteById(id);
    }
}
