package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.AutoMobileResponseDTO;
import com.eclectics.Garage.dto.AutomobileRequestsDTO;
import com.eclectics.Garage.model.AutoMobiles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AutoMobileMapper {
    AutoMobileMapper INSTANCE = Mappers.getMapper(AutoMobileMapper.class);

    @Mapping(target = "id", ignore = true)
    AutoMobiles toEntity(AutomobileRequestsDTO dto);
    AutoMobileResponseDTO toResponseDTO(AutoMobiles entity);
    List<AutoMobileResponseDTO> toResponseDTOList(List<AutoMobiles> autoMobiles);
    void updateEntityFromDTO(AutomobileRequestsDTO dto, @MappingTarget AutoMobiles entity);
}
