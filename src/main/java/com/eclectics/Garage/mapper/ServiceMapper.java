package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.model.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    @Mapping(target = "id", ignore = true)
    Service toEntity(ServiceRequestDTO dto);

    ServiceResponseDTO toResponseDTO(Service entity);

    List<ServiceResponseDTO> toResponseDTOList(List<Service> services);

    default Optional<ServiceResponseDTO> toOptionalResponseDTO(Optional<Service> entity) {
        return entity.map(this::toResponseDTO);
    }

    @Mapping(target = "id", ignore = true)
    void updateFromDTO(ServiceRequestDTO serviceRequestDTO, @MappingTarget Service entity);
}
