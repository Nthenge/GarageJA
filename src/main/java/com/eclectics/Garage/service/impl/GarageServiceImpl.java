package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.mapper.GarageMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.GarageService;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@CacheConfig(cacheNames = {"garages"})
public class GarageServiceImpl implements GarageService {

    private final GarageRepository garageRepository;
    private final AuthenticationService authenticationService;
    private final GarageMapper mapper;

    public GarageServiceImpl(GarageRepository garageRepository, AuthenticationService authenticationService, GarageMapper mapper) {
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
    }

    public ProfileCompleteDTO checkProfileCompletion(Garage garage) {
        List<String> missingFields = garage.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    @Override
    @CacheEvict(value = {"allGarages"}, allEntries = true)
    public Garage createGarage(GarageRequestsDTO garageRequestsDTO, MultipartFile businessLicense,
                               MultipartFile professionalCertificate, MultipartFile facilityPhotos) throws IOException {

        Garage garage = mapper.toEntity(garageRequestsDTO);
        User user = authenticationService.getCurrentUser();
        garage.setUser(user);

        Optional<Garage> existingGarage = garageRepository.findByBusinessName(garage.getBusinessName());
        if (existingGarage.isPresent()) {
            throw new ResourceNotFoundException("Garage with this name exists");
        }

        if (businessLicense != null && !businessLicense.isEmpty()) {
            garage.setBusinessLicense(businessLicense.getBytes());
        }
        if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
            garage.setProfessionalCertificate(professionalCertificate.getBytes());
        }
        if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
            garage.setFacilityPhotos(facilityPhotos.getBytes());
        }

        boolean uniqueAdminIdExists;
        long uniqueAdminId;
        do {
            Random random = new Random();
            uniqueAdminId = random.nextInt(90000) + 10000;
            uniqueAdminIdExists = garageRepository.findByGarageId(uniqueAdminId).isPresent();
        } while (uniqueAdminIdExists);

        garage.setGarageId(uniqueAdminId);

        Garage saved = garageRepository.save(garage);
        return saved;
    }

    @Override
    @Cacheable(value = "garageByUser", key = "#userId")
    public Optional<GarageResponseDTO> findByUserId(Long userId) {
        return garageRepository.findByUserId(userId).map(mapper::toResponseDTO);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return garageRepository.findByUserId(userId)
                .map(Garage::isComplete)
                .orElse(false);
    }

    @Override
    @Cacheable(value = "garageById", key = "#garageId")
    public Optional<GarageResponseDTO> getGarageById(Long garageId) {
        return garageRepository.findByGarageId(garageId).map(mapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "garageByName", key = "#name")
    public Optional<GarageResponseDTO> getGarageByName(String name) {
        return garageRepository.findByBusinessName(name).map(mapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "allGarages")
    public List<GarageResponseDTO> getAllGarages() {
        return mapper.toResponseDTOList(garageRepository.findAll());
    }

    @Override
    @CachePut(value = "garageById", key = "#garageId")
    @CacheEvict(value = {"allGarages", "garageByUser", "garageByName"}, allEntries = true)
    public GarageResponseDTO updateGarage(Long garageId, GarageRequestsDTO garageRequestsDTO,
                                          MultipartFile businessLicense, MultipartFile professionalCertificate,
                                          MultipartFile facilityPhotos) {

        return garageRepository.findByGarageId(garageId).map(existingGarage -> {
            if (garageRequestsDTO.getOperatingHours() != null)
                existingGarage.setOperatingHours(garageRequestsDTO.getOperatingHours());
            if (garageRequestsDTO.getBusinessRegNumber() != null)
                existingGarage.setBusinessRegNumber(garageRequestsDTO.getBusinessRegNumber());
            if (garageRequestsDTO.getBusinessEmailAddress() != null)
                existingGarage.setBusinessEmailAddress(garageRequestsDTO.getBusinessEmailAddress());
            if (garageRequestsDTO.getTwentyFourHours() != null)
                existingGarage.setTwentyFourHours(garageRequestsDTO.getTwentyFourHours());
            if (garageRequestsDTO.getServiceCategories() != null)
                existingGarage.setServiceCategories(garageRequestsDTO.getServiceCategories());
            if (garageRequestsDTO.getSpecialisedServices() != null)
                existingGarage.setSpecialisedServices(garageRequestsDTO.getSpecialisedServices());
            if (garageRequestsDTO.getBusinessName() != null)
                existingGarage.setBusinessName(garageRequestsDTO.getBusinessName());
            if (garageRequestsDTO.getPhysicalBusinessAddress() != null)
                existingGarage.setPhysicalBusinessAddress(garageRequestsDTO.getPhysicalBusinessAddress());
            if (garageRequestsDTO.getBusinessPhoneNumber() != null)
                existingGarage.setBusinessPhoneNumber(garageRequestsDTO.getBusinessPhoneNumber());
            if (garageRequestsDTO.getYearsInOperation() != null)
                existingGarage.setYearsInOperation(garageRequestsDTO.getYearsInOperation());
            if (garageRequestsDTO.getMpesaPayBill() != null)
                existingGarage.setMpesaPayBill(garageRequestsDTO.getMpesaPayBill());
            if (garageRequestsDTO.getMpesaTill() != null)
                existingGarage.setMpesaTill(garageRequestsDTO.getMpesaTill());
            try {
                if (businessLicense != null && !businessLicense.isEmpty()) {
                    existingGarage.setBusinessLicense(businessLicense.getBytes());
                }
                if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
                    existingGarage.setProfessionalCertificate(professionalCertificate.getBytes());
                }
                if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
                    existingGarage.setFacilityPhotos(facilityPhotos.getBytes());
                }
            } catch (IOException e) {
                throw new BadRequestException("Failed to read file data");
            }

            Garage updatedGarage = garageRepository.save(existingGarage);
            return mapper.toResponseDTO(updatedGarage);

        }).orElseThrow(() -> new ResourceNotFoundException("Garage not found"));
    }

    @Override
    @CacheEvict(value = {"allGarages", "garageById", "garageByName", "garageByUser"}, allEntries = true)
    public void deleteGarage(Long id) {
        if (!garageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Garage with id " + id + " does not exist");
        }
        garageRepository.deleteById(id);
    }
}
