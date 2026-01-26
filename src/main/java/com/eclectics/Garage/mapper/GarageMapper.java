package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.model.Garage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GarageMapper {
    GarageMapper INSTANCE = Mappers.getMapper(GarageMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "licenseNumber", ignore = true)
    @Mapping(target = "professionalCertificate", ignore = true)
    @Mapping(target = "facilityPhotos", ignore = true)
    @Mapping(source = "businessLocation", target = "businessLocation")
    Garage toEntity(GarageRequestsDTO dto);

    @Mapping(source = "businessLocation.latitude", target = "latitude")
    @Mapping(source = "businessLocation.longitude", target = "longitude")
    GarageResponseDTO toResponseDTO(Garage entity);

    List<GarageResponseDTO> toResponseDTOList(List<Garage> garages);
}