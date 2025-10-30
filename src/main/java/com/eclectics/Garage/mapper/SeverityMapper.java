package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.SeverityRequestDTO;
import com.eclectics.Garage.dto.SeverityResponseDTO;
import com.eclectics.Garage.model.SeverityCategories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeverityMapper {
    SeverityMapper INSTANCE = Mappers.getMapper(SeverityMapper.class);

    @Mapping(target = "id", ignore = true)
    SeverityCategories toEntity(SeverityRequestDTO dto);
    SeverityResponseDTO toResponseDTO(SeverityCategories entity);
    List<SeverityResponseDTO> toResponseListDTO(List<SeverityCategories> severityCategories);
}
