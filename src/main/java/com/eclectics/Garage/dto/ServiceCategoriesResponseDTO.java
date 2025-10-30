package com.eclectics.Garage.dto;

public class ServiceCategoriesResponseDTO {

    private String serviceCategoryName;

    public ServiceCategoriesResponseDTO() {
    }

    public ServiceCategoriesResponseDTO(String serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }

    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }
}
