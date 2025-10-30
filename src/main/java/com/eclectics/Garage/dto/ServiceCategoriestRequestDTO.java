package com.eclectics.Garage.dto;

public class ServiceCategoriestRequestDTO {

    private String serviceCategoryName;

    public ServiceCategoriestRequestDTO() {
    }

    public ServiceCategoriestRequestDTO(String serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }

    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }
}
