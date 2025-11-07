package com.eclectics.Garage.mapper;

import com.eclectics.Garage.dto.UserDetailsAuthDTO;
import com.eclectics.Garage.dto.UserRegistrationRequestDTO;
import com.eclectics.Garage.dto.UserRegistrationResponseDTO;
import com.eclectics.Garage.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRegistrationRequestDTO dto);
    UserRegistrationResponseDTO toResponseDTO(User entity);
    default Optional<UserRegistrationResponseDTO> toOptionalResponse(Optional<User> entity){
        return entity.map(this::toResponseDTO);
    };
    List<UserRegistrationResponseDTO> toResponseList(List<User> entity);
    @Mapping(source = "firstname", target = "firstname")
    UserDetailsAuthDTO toAuthDetailsResponseDTO(User user);

}
