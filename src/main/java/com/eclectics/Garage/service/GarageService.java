package com.eclectics.Garage.service;
import com.eclectics.Garage.model.Garage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface GarageService {
    Garage createGarage(Garage garage);
    Garage uploadDocument(Long garageId, MultipartFile businessLicense, MultipartFile professionalCertificate, MultipartFile facilityPhotos) throws java.io.IOException;
    Optional<Garage> getGarageById(Long garageId);
    Optional<Garage> getGarageByName(String name);
    List<Garage> getAllGarages();
    Garage updateGarage(Long id, Garage garage);
    void deleteGarage(Long id);
}

