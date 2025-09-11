package com.eclectics.Garage.service;

import com.eclectics.Garage.model.ServiceCategories;

import java.util.List;

public interface ServiceCategoriesService {
    ServiceCategories createCategory(ServiceCategories serviceCategories);
    List<ServiceCategories> getAllServiceCategories();
    ServiceCategories getServiceCategoryByName(String getServiceCategoryByName);
}
