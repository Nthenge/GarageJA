package com.eclectics.Garage.service;
import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.model.Garage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface GarageService {
    Garage createGarage(GarageRequestsDTO garageRequestsDTO, MultipartFile businessLicense, MultipartFile professionalCertificate, MultipartFile facilityPhotos) throws java.io.IOException;
    Optional<GarageResponseDTO> findByUserId(Long userId);
    boolean isDetailsCompleted(Long userId);
    Optional<GarageResponseDTO> getGarageById(Long garageId);
    Optional<GarageResponseDTO> getGarageByName(String name);
    List<GarageResponseDTO> getAllGarages();
    GarageResponseDTO updateGarage(Long id, GarageRequestsDTO garageRequestsDTO, MultipartFile businessLicense, MultipartFile professionalCertificate, MultipartFile facilityPhotos);
    void deleteGarage(Long id);
}

