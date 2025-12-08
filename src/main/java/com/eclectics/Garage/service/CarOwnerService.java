package com.eclectics.Garage.service;


import com.eclectics.Garage.dto.CarOwnerLocationUpdateDTO;
import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.User;
import io.jsonwebtoken.io.IOException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CarOwnerService {
    CarOwner createCarOwner(CarOwnerRequestsDTO carOwnerRequestsDTO, MultipartFile profilePic)throws java.io.IOException;
    List<CarOwnerResponseDTO> filterCarOwners(
            String licensePlate,
            Integer uniqueId
    );

    boolean isDetailsCompleted(Long userId);
    CarOwnerResponseDTO updateOwnProfile(CarOwnerRequestsDTO carOwnerRequestsDTO,
                                         MultipartFile profilePic) throws java.io.IOException;

    @Transactional
    CarOwner updateCarOwnerLiveLocation(Long carOwnerId, CarOwnerLocationUpdateDTO locationDto);

    String deleteCarOwner(Long id);
    Optional<String> getProfilePictureUrlByUniqueId(Integer uniqueId, int expiryMinutes);
}

