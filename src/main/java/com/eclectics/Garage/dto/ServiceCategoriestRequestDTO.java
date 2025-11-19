package com.eclectics.Garage.dto;

public class ServiceCategoriestRequestDTO {

    private String serviceCategoryName;
    private String description;
    private String icon;

    public ServiceCategoriestRequestDTO() {
    }

    public ServiceCategoriestRequestDTO(String serviceCategoryName, String description, String icon) {
        this.serviceCategoryName = serviceCategoryName;
        this.description = description;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }
}
