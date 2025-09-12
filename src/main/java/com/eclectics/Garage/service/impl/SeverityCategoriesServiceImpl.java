package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.SeverityCategories;
import com.eclectics.Garage.repository.SeverityCategoryRepository;
import com.eclectics.Garage.service.SeverityCategoriesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeverityCategoriesServiceImpl implements SeverityCategoriesService {

    private final SeverityCategoryRepository severityCategoryRepository;

    public SeverityCategoriesServiceImpl(SeverityCategoryRepository severityCategoryRepository) {
        this.severityCategoryRepository = severityCategoryRepository;
    }

    @Override
    public SeverityCategories createCategory(SeverityCategories severityCategories) {
        return severityCategoryRepository.save(severityCategories);
    }

    @Override
    public List<SeverityCategories> getAllSeverityCategories() {
        return severityCategoryRepository.findAll();
    }

    @Override
    public SeverityCategories getSeverityCategoryByName(String severityName) {
        return severityCategoryRepository.findBySeverityName(severityName)
                .orElseThrow(()-> new RuntimeException("Severity List by name"));
    }

}
