package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.PaymentsRequetsDTO;
import com.eclectics.Garage.dto.PaymentsResponseDTO;
import com.eclectics.Garage.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PaymentsMapper {
    PaymentsMapper INSTANCE = Mappers.getMapper(PaymentsMapper.class);

    Payment toEntity(PaymentsRequetsDTO dto);
    PaymentsResponseDTO toResponseDTO(Payment entity);
    List<PaymentsResponseDTO> toResponseDTOList(List<Payment> payments);
    default Optional<PaymentsResponseDTO> toOptionalResponse(Optional<Payment> entity){
        return entity.map(this::toResponseDTO);
    };
}
