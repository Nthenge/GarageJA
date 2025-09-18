package com.eclectics.Garage.service;


import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.model.CarOwner;
import io.jsonwebtoken.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CarOwnerService {
    CarOwner createCarOwner(CarOwnerRequestsDTO carOwnerRequestsDTO, MultipartFile profilePic)throws java.io.IOException;
    Optional<CarOwnerResponseDTO> findByUserId(Long userId);
    boolean isDetailsCompleted(Long userId);
    List<CarOwnerResponseDTO> getAllCarOwners();
    CarOwnerResponseDTO updateCarOwner(Long id, CarOwnerRequestsDTO carOwnerRequestsDTO);
    CarOwnerResponseDTO updateProfilePic(Integer carOwnerUniqueId, MultipartFile profilePic) throws java.io.IOException;
    String deleteCarOwner(Long id);
    Optional<CarOwnerResponseDTO> getCarOwnerByUniqueId(Integer uniqueId);
}

