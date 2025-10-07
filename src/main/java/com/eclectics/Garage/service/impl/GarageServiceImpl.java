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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GarageServiceImpl implements GarageService {

    private final GarageRepository garageRepository;
    private final AuthenticationService authenticationService;
    private final GarageMapper mapper;

    public GarageServiceImpl(GarageRepository garageRepository, AuthenticationService authenticationService, GarageMapper mapper) {
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
    }


    public ProfileCompleteDTO checkProfileCompletion(Garage garage){
        List<String> missingFields = garage.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    @Override
    public Garage createGarage(GarageRequestsDTO garageRequestsDTO, MultipartFile businessLicense, MultipartFile professionalCertificate, MultipartFile facilityPhotos)throws java.io.IOException {

        Garage garage = mapper.toEntity(garageRequestsDTO);
        User userid = authenticationService.getCurrentUser();
        garage.setUser(userid);

        Optional<Garage> GarageExists = garageRepository.findByBusinessName(garage.getBusinessName());
        if (businessLicense != null && !businessLicense.isEmpty()) {
            garage.setBusinessLicense(businessLicense.getBytes());
        }

        if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
            garage.setProfessionalCertificate(professionalCertificate.getBytes());
        }

        if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
            garage.setFacilityPhotos(facilityPhotos.getBytes());
        }
        if (GarageExists.isPresent()){
            throw new RuntimeException("Garage with this name exists");
        }

        boolean uniqueAdminIdExists;
        long uniqueAdminId;

        do {
            Random random = new Random();
            uniqueAdminId = random.nextInt(90000)+ 10000;

            uniqueAdminIdExists = garageRepository.findByGarageId(uniqueAdminId).isPresent();
            if (uniqueAdminIdExists){
                throw new RuntimeException("A garage with this Admin id already exist");
            }

        }while (uniqueAdminIdExists);

        garage.setGarageId(uniqueAdminId);

        return garageRepository.save(garage);
    }

    @Override
    public Optional<GarageResponseDTO> findByUserId(Long userId) {
        Optional<Garage> garage = garageRepository.findByUserId(userId);
        return garage.map(mapper::toResponseDTO);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return garageRepository.findByUserId(userId)
                .map(Garage::isComplete) // delegate to entity method
                .orElse(false);
    }

    @Override
    public Optional<GarageResponseDTO> getGarageById(Long garageId) {
        Optional<Garage> garage = garageRepository.findByGarageId(garageId);
        return garage.map(mapper::toResponseDTO);
    }

    @Override
    public Optional<GarageResponseDTO> getGarageByName(String name) {
        Optional<Garage> garage = garageRepository.findByBusinessName(name);
        return garage.map(mapper::toResponseDTO);
    }


    @Override
    public List<GarageResponseDTO> getAllGarages() {
        List<Garage> allGarages =garageRepository.findAll();
        return mapper.toResponseDTOList(allGarages);
    }

    @Override
    public GarageResponseDTO updateGarage(Long garageId, GarageRequestsDTO garageRequestsDTO) {
        return garageRepository.findByGarageId(garageId).map(eg -> {
            if (garageRequestsDTO.getGarageId() != null) eg.setGarageId(garageRequestsDTO.getGarageId());
            if (garageRequestsDTO.getOperatingHours() != null) eg.setOperatingHours(garageRequestsDTO.getOperatingHours());
            if (garageRequestsDTO.getBusinessRegNumber() != null) eg.setBusinessRegNumber(garageRequestsDTO.getBusinessRegNumber());
            if (garageRequestsDTO.getBusinessEmailAddress() != null) eg.setBusinessEmailAddress(garageRequestsDTO.getBusinessEmailAddress());
            if (garageRequestsDTO.getTwentyFourHours() != null) eg.setTwentyFourHours(garageRequestsDTO.getTwentyFourHours());
            if (garageRequestsDTO.getServiceCategories() != null) eg.setServiceCategories(garageRequestsDTO.getServiceCategories());
            if (garageRequestsDTO.getSpecialisedServices() != null) eg.setSpecialisedServices(garageRequestsDTO.getSpecialisedServices());
            if (garageRequestsDTO.getBusinessName() != null) eg.setBusinessName(garageRequestsDTO.getBusinessName());
            if (garageRequestsDTO.getPhysicalBusinessAddress() != null) eg.setPhysicalBusinessAddress(garageRequestsDTO.getPhysicalBusinessAddress());
            if (garageRequestsDTO.getBusinessPhoneNumber() != null) eg.setBusinessPhoneNumber(garageRequestsDTO.getBusinessPhoneNumber());
            if (garageRequestsDTO.getYearsInOperation() != null) eg.setYearsInOperation(garageRequestsDTO.getYearsInOperation());
            if (garageRequestsDTO.getMpesaPayBill() != null) eg.setMpesaPayBill(garageRequestsDTO.getMpesaPayBill());
            if (garageRequestsDTO.getMpesaTill() != null) eg.setMpesaTill(garageRequestsDTO.getMpesaTill());

            //binary data
            if (garageRequestsDTO.getBusinessLicense() != null && garageRequestsDTO.getBusinessLicense().length > 0) eg.setBusinessLicense(garageRequestsDTO.getBusinessLicense());
            if (garageRequestsDTO.getProfessionalCertificate() != null && garageRequestsDTO.getProfessionalCertificate().length > 0) eg.setProfessionalCertificate(garageRequestsDTO.getProfessionalCertificate());
            if (garageRequestsDTO.getFacilityPhotos() != null && garageRequestsDTO.getFacilityPhotos().length > 0) eg.setFacilityPhotos(garageRequestsDTO.getFacilityPhotos());

            Garage garageUpdate = garageRepository.save(eg);
            return mapper.toResponseDTO(garageUpdate);
        }).orElseThrow(()-> new RuntimeException("Garage not found"));
    }


    @Override
    public void deleteGarage(Long id) {
        if (!garageRepository.existsById(id)){
            throw new RuntimeException("Garage with id " + id + " does not exist");
        }
        garageRepository.deleteById(id);
    }
}
