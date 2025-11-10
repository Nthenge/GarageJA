package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.ServiceRequestsRequestDTO;
import com.eclectics.Garage.dto.ServiceRequestsResponseDTO;
import com.eclectics.Garage.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;


@Mapper(componentModel = "spring")
public interface ServiceRequestsMapper {

    ServiceRequestsMapper INSTANCE = Mappers.getMapper(ServiceRequestsMapper.class);

    @Mapping(target = "id", ignore = true)
    ServiceRequest toEntity(ServiceRequestsRequestDTO dto);

    @Mapping(target = "carId", source = "carOwner.uniqueId")
    @Mapping(target = "garageId", source = "garage.garageId")
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "severityId", source = "severityCategories.id")
    @Mapping(target = "garageName", source = "garage.businessName")
    @Mapping(target = "serviceName", source = "service.serviceName")
    @Mapping(target = "severityName", source = "severityCategories.severityName")
    ServiceRequestsResponseDTO toResponse(ServiceRequest entity);

    List<ServiceRequestsResponseDTO> toResponseList(List<ServiceRequest> serviceRequests);

    default Optional<ServiceRequestsResponseDTO> toOptionalResponse(Optional<ServiceRequest> entity) {
        return entity.map(this::toResponse);
    }

    void updateFromDTO(ServiceRequestsRequestDTO dto, @MappingTarget ServiceRequest entity);
}

