package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.ServiceCategoriesResponseDTO;
import com.eclectics.Garage.dto.ServiceCategoriestRequestDTO;
import com.eclectics.Garage.model.ServiceCategories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceCategoriesMapper {
    ServiceCategoriesMapper INSTANCE = Mappers.getMapper(ServiceCategoriesMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "services", ignore = true)
    ServiceCategories toEntity(ServiceCategoriestRequestDTO dto);
    ServiceCategoriesResponseDTO toResponseDTO(ServiceCategories entity);
    List<ServiceCategoriesResponseDTO> toResponseDTOList(List<ServiceCategories> serviceCategories);
}
