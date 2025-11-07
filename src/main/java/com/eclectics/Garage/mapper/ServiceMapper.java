package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.model.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "garage", ignore = true)
    @Mapping(target = "serviceCategories", ignore = true)
    Service toEntity(ServiceRequestDTO dto);

    @Mapping(source = "garage.garageId", target = "garageId")
    @Mapping(source = "garage.businessName", target = "garageName")
    @Mapping(source = "serviceCategories.id", target = "categoryId")
    @Mapping(source = "serviceCategories.serviceCategoryName", target = "categoryName")
    ServiceResponseDTO toResponseDTO(Service entity);

    List<ServiceResponseDTO> toResponseDTOList(List<Service> services);

    default Optional<ServiceResponseDTO> toOptionalResponseDTO(Optional<Service> entity) {
        return entity.map(this::toResponseDTO);
    }

    @Mapping(target = "id", ignore = true)
    void updateFromDTO(ServiceRequestDTO dto, @MappingTarget Service entity);
}

