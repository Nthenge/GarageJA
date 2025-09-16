package com.eclectics.Garage.service;


import com.eclectics.Garage.model.CarOwner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CarOwnerService {
    CarOwner createCarOwner(CarOwner carOwner);

    Optional<CarOwner> findByUserId(Long userId);

    boolean isDetailsCompleted(Long userId);

    CarOwner uploadDocument(Integer uniqueId, MultipartFile profilePic) throws java.io.IOException;
    Optional<CarOwner> getCarOwnerById(Long id);
    Optional<CarOwner> getCarOwnerByEmail(String email);
    List<CarOwner> getAllCarOwners();
    CarOwner updateCarOwner(Long id, CarOwner carOwner);
    String deleteCarOwner(Long id);
    Optional<CarOwner> getCarOwnerByUniqueId(Integer uniqueId);
}

