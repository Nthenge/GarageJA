package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.model.CarOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarOwnerMapper {

    CarOwnerMapper INSTANCE = Mappers.getMapper(CarOwnerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uniqueId", ignore = true)
    @Mapping(target = "profilePic", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "requests", ignore = true)
    CarOwner toEntity(CarOwnerRequestsDTO dto);

    CarOwnerResponseDTO toDto(CarOwner entity);
}
