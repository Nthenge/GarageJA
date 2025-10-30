package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.ServiceRequestsRequestDTO;
import com.eclectics.Garage.dto.ServiceRequestsResponseDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ServiceRequestsMapper {

    ServiceRequestsMapper INSTANCE = Mappers.getMapper(ServiceRequestsMapper.class);


    @Mapping(target = "id", ignore = true)
    ServiceRequest toEntity(ServiceRequestsRequestDTO dto);

    ServiceRequestsResponseDTO toResponse(ServiceRequest entity);

    List<ServiceRequestsResponseDTO> toResponseList(List<ServiceRequest> serviceRequests);

    default Optional<ServiceRequestsResponseDTO> toOptionalResponse(Optional<ServiceRequest> entity){
        return entity.map(this::toResponse);
    };

    void updateFromDTO(ServiceRequestsRequestDTO serviceRequestDTO, @MappingTarget ServiceRequest entity);
}
