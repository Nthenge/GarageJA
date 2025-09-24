package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.model.Mechanic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MechanicMapper {
    MechanicMapper INSTANCE = Mappers.getMapper(MechanicMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "garage", ignore = true)
    @Mapping(target = "user", ignore = true)
    Mechanic toEntity(MechanicRequestDTO dto);
    MechanicRequestDTO toDTO(Mechanic entity);
    MechanicResponseDTO toResponseDTO(Mechanic entity);
    List<MechanicResponseDTO> toResponseDTOList(List<Mechanic> mechanics);
    void updateEntityFromDTO(MechanicRequestDTO dto, @MappingTarget Mechanic entity);
}
