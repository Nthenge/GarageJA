package com.eclectics.Garage.service;

import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.model.SeverityCategories;

import java.util.List;

public interface SeverityCategoriesService {
    SeverityCategories createCategory(SeverityCategories severityCategories);
    List<SeverityCategories> getAllSeverityCategories();
    SeverityCategories getSeverityCategoryByName(String severityName);
}
