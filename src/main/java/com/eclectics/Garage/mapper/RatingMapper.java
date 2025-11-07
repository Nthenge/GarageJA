package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.RatingRequestsDTO;
import com.eclectics.Garage.dto.RatingResponseDTO;
import com.eclectics.Garage.model.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceRequest", ignore = true)
    @Mapping(target = "garage", ignore = true)
    @Mapping(target = "mechanic", ignore = true)
    Rating toEntity(RatingRequestsDTO dto);

    RatingResponseDTO toResponse(Rating entity);

    void updateFromDTO(RatingRequestsDTO dto, @MappingTarget Rating entity);
}
