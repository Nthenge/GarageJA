package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.SeverityRequestDTO;
import com.eclectics.Garage.dto.SeverityResponseDTO;
import com.eclectics.Garage.model.SeverityCategories;

import java.util.List;

public interface SeverityCategoriesService {
    SeverityCategories createCategory(SeverityRequestDTO severityRequestDTO);
    List<SeverityResponseDTO> getAllSeverityCategories();
    SeverityResponseDTO getSeverityCategoryByName(String severityName);
}
