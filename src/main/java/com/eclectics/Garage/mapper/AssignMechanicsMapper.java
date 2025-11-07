package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.AssignMechanicsRequestsDTO;
import com.eclectics.Garage.dto.AssignMechanicsResponseDTO;
import com.eclectics.Garage.model.AssignMechanics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssignMechanicsMapper {
    AssignMechanicsMapper INSTANCE = Mappers.getMapper(AssignMechanicsMapper.class);

    @Mapping(target = "id", ignore = true)
    AssignMechanics toEntity(AssignMechanicsRequestsDTO dto);
    AssignMechanicsResponseDTO toResponseDTO(AssignMechanics entity);
    List<AssignMechanicsResponseDTO> toResponseList(List<AssignMechanics> assignMechanics );
}
