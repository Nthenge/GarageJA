package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.model.Garage;
import jdk.dynalink.linker.LinkerServices;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface GarageMapper {
    GarageMapper INSTANCE = Mappers.getMapper(GarageMapper.class);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Garage toEntity(GarageRequestsDTO dto);
    GarageResponseDTO toResponseDTO(Garage entity);
    List<GarageResponseDTO> toResponseDTOList(List<Garage> garages);
}
