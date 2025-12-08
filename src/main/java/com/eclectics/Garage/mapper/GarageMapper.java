package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.model.Garage;
// import jdk.dynalink.linker.LinkerServices; // Not needed, can be removed
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GarageMapper {
    GarageMapper INSTANCE = Mappers.getMapper(GarageMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "businessLicense", ignore = true)
    @Mapping(target = "professionalCertificate", ignore = true)
    @Mapping(target = "facilityPhotos", ignore = true)
    // You should also ignore the new businessLocation field here if you want to be explicit,
    // though MapStruct usually ignores complex types if not mapped.
    @Mapping(target = "businessLocation", ignore = true)
    Garage toEntity(GarageRequestsDTO dto);

    @Mapping(source = "businessLocation.latitude", target = "latitude")
    @Mapping(source = "businessLocation.longitude", target = "longitude")
    GarageResponseDTO toResponseDTO(Garage entity);

    List<GarageResponseDTO> toResponseDTOList(List<Garage> garages);
}