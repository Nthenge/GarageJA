package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.GarageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GarageServiceImpl implements GarageService {

    public GarageServiceImpl(GarageRepository garageRepository, UsersRepository usersRepository, AuthenticationService authenticationService) {
        this.garageRepository = garageRepository;
        this.usersRepository = usersRepository;
        this.authenticationService = authenticationService;
    }

    private final GarageRepository garageRepository;
    private final UsersRepository usersRepository;
    private final AuthenticationService authenticationService;

    @Override
    public Garage createGarage(Garage garage) {

        User userid = authenticationService.getCurrentUser();
        garage.setUser(userid);

        Optional<Garage> GarageExists = garageRepository.findByBusinessName(garage.getBusinessName());
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
    public Optional<Garage> findByUserId(Long userId) {
        return garageRepository.findByUserId(userId);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return garageRepository.findByUserId(userId)
                .map(Garage::isComplete) // delegate to entity method
                .orElse(false);
    }

    @Override
    public Garage uploadDocument(Long garageId, MultipartFile businessLicense, MultipartFile professionalCertificate, MultipartFile facilityPhotos) throws IOException {
        Garage garage = garageRepository.findByGarageId(garageId).orElseThrow(()-> new RuntimeException("Garage with this id not found"));

        if (businessLicense != null && !businessLicense.isEmpty()) {
            garage.setBusinessLicense(businessLicense.getBytes());
        }

        if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
            garage.setProfessionalCertificate(professionalCertificate.getBytes());
        }

        if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
            garage.setFacilityPhotos(facilityPhotos.getBytes());
        }

        return garageRepository.save(garage);
    }

    @Override
    public Optional<Garage> getGarageById(Long garageId) {
        return garageRepository.findByGarageId(garageId);
    }

    @Override
    public Optional<Garage> getGarageByName(String name) {
        return garageRepository.findByBusinessName(name);
    }


    @Override
    public List<Garage> getAllGarages() {
        return garageRepository.findAll();
    }

    @Override
    public Garage updateGarage(Long garageId, Garage garage) {
        return garageRepository.findByGarageId(garageId).map(eg -> {
            if (garage.getGarageId() != null) eg.setGarageId(garage.getGarageId());
            if (garage.getOperatingHours() != null) eg.setOperatingHours(garage.getOperatingHours());
            if (garage.getBusinessRegNumber() != null) eg.setBusinessRegNumber(garage.getBusinessRegNumber());
            if (garage.getBusinessEmailAddress() != null) eg.setBusinessEmailAddress(garage.getBusinessEmailAddress());
            if (garage.getTwentyFourHours() != null) eg.setTwentyFourHours(garage.getTwentyFourHours());
            if (garage.getServiceCategories() != null) eg.setServiceCategories(garage.getServiceCategories());
            if (garage.getSpecialisedServices() != null) eg.setSpecialisedServices(garage.getSpecialisedServices());
            if (garage.getBusinessName() != null) eg.setBusinessName(garage.getBusinessName());
            if (garage.getPhysicalBusinessAddress() != null) eg.setPhysicalBusinessAddress(garage.getPhysicalBusinessAddress());
            if (garage.getBusinessPhoneNumber() != null) eg.setBusinessPhoneNumber(garage.getBusinessPhoneNumber());
            if (garage.getYearsInOperation() != null) eg.setYearsInOperation(garage.getYearsInOperation());
            if (garage.getMpesaPayBill() != null) eg.setMpesaPayBill(garage.getMpesaPayBill());
            if (garage.getMpesaTill() != null) eg.setMpesaTill(garage.getMpesaTill());

            //binary data
            if (garage.getBusinessLicense() != null && garage.getBusinessLicense().length > 0) eg.setBusinessLicense(garage.getBusinessLicense());
            if (garage.getProfessionalCertificate() != null && garage.getProfessionalCertificate().length > 0) eg.setProfessionalCertificate(garage.getProfessionalCertificate());
            if (garage.getFacilityPhotos() != null && garage.getFacilityPhotos().length > 0) eg.setFacilityPhotos(garage.getFacilityPhotos());

            return garageRepository.save(eg);
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
