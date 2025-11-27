package com.eclectics.Garage.service;
import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.model.Garage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface GarageService {
    ProfileCompleteDTO checkProfileCompletion(Garage garage);

    Garage createGarage(GarageRequestsDTO garageRequestsDTO, MultipartFile businessLicense, MultipartFile professionalCertificate, MultipartFile facilityPhotos) throws java.io.IOException;
    boolean isDetailsCompleted(Long userId);

    List<GarageResponseDTO> filterGarages(
            String businessName,
            String physicalBusinessAddress
    );

    long countAllGarages();
    GarageResponseDTO updateOwnGarage(GarageRequestsDTO dto,
                                   MultipartFile businessLicense,
                                   MultipartFile professionalCertificate,
                                   MultipartFile facilityPhotos);
    void deleteGarage(Long id);
    Optional<String> getGarageUrlByUniqueId(Long uniqueId, int expiryMinutes);
}

