package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.exception.GarageExceptions.FailedToReadMultiPartFile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MechanicMapper {
    MechanicMapper INSTANCE = Mappers.getMapper(MechanicMapper.class);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "garage", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "profilePic", ignore = true)
    @Mapping(target = "nationalIDPic",ignore = true)
    @Mapping(target = "professionalCertfificate",ignore = true)
    @Mapping(target = "anyRelevantCertificate",ignore = true)
    @Mapping(target = "policeClearanceCertficate",ignore = true)
    Mechanic toEntity(MechanicRequestDTO dto);
    MechanicResponseDTO toResponseDTO(Mechanic entity);
    List<MechanicResponseDTO> toResponseDTOList(List<Mechanic> mechanics);
    void updateEntityFromDTO(MechanicRequestDTO dto, @MappingTarget Mechanic entity);
}
