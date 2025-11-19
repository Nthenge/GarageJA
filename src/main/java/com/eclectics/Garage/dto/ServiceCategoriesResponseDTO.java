package com.eclectics.Garage.dto;

public class ServiceCategoriesResponseDTO {

    private Long id;
    private String serviceCategoryName;
    private String description;
    private String icon;

    public ServiceCategoriesResponseDTO() {
    }

    public ServiceCategoriesResponseDTO(Long id, String serviceCategoryName, String description, String icon) {
        this.id = id;
        this.serviceCategoryName = serviceCategoryName;
        this.description = description;
        this.icon = icon;
    }

    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
