package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.ServiceCategoriesResponseDTO;
import com.eclectics.Garage.dto.ServiceCategoriestRequestDTO;
import com.eclectics.Garage.model.ServiceCategories;

import java.util.List;

public interface ServiceCategoriesService {
    ServiceCategories createCategory(ServiceCategoriestRequestDTO serviceCategoriestRequestDTO);
    List<ServiceCategoriesResponseDTO> getAllServiceCategories();
    ServiceCategoriesResponseDTO getServiceCategoryByName(String getServiceCategoryByName);
    void delete(Long id);
}
