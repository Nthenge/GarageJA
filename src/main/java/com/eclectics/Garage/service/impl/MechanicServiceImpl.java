package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.MechanicService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class MechanicServiceImpl implements MechanicService {

    private final MechanicRepository mechanicRepository;
    private final GarageRepository garageRepository;
    private final UsersRepository usersRepository;
    private final AuthenticationService authenticationService;

    public MechanicServiceImpl(MechanicRepository mechanicRepository, GarageRepository garageRepository, UsersRepository usersRepository, AuthenticationService authenticationService) {
        this.mechanicRepository = mechanicRepository;
        this.garageRepository = garageRepository;
        this.usersRepository = usersRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public Optional<Mechanic> findByUserId(Long userId) {
        return mechanicRepository.findByUserId(userId);
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return mechanicRepository.findByUserId(userId)
                .map(Mechanic::isComplete) // calls the entity method
                .orElse(false);
    }

    @Override
    public Mechanic createMechanic(Mechanic mechanic) {

        User userid = authenticationService.getCurrentUser();
        mechanic.setUser(userid);

        Optional<Mechanic> mechanicExist = mechanicRepository.findMechanicByNationalIdNumber(mechanic.getNationalIdNumber());
        if (mechanicExist.isPresent()) {
            throw new RuntimeException("Mechanic with this national ID already exist");
        }

        if (mechanic.getGarage() != null && mechanic.getGarage().getGarageId() != null) {
            Long garageAdminId = mechanic.getGarage().getGarageId();
            Garage garage = garageRepository.findByGarageId(garageAdminId)
                    .orElseThrow(() -> new RuntimeException("Garage with this id " + garageAdminId + " not found"));

            mechanic.setGarage(garage);
        }
        return mechanicRepository.save(mechanic);
    }

    @Override
    public Mechanic uploadDocuments(Long id, MultipartFile profilepic, MultipartFile nationalIdFile, MultipartFile profCert, MultipartFile anyRelCert, MultipartFile polCleCert) throws java.io.IOException {
        Mechanic mechanic = mechanicRepository.findById(id).orElseThrow(() -> new RuntimeException("Mechanic not found"));

        if (profilepic != null && !profilepic.isEmpty()) {
            mechanic.setProfilePic(profilepic.getBytes());
        }

        if (nationalIdFile != null && !nationalIdFile.isEmpty()) {
            mechanic.setNationalIDPic(nationalIdFile.getBytes());
        }

        if (profCert != null && !profCert.isEmpty()) {
            mechanic.setProfessionalCertfificate(profCert.getBytes());
        }

        if (anyRelCert != null && !anyRelCert.isEmpty()) {
            mechanic.setAnyRelevantCertificate(anyRelCert.getBytes());
        }

        if (polCleCert != null && !polCleCert.isEmpty()) {
            mechanic.setPoliceClearanceCertficate(polCleCert.getBytes());
        }

        return mechanicRepository.save(mechanic);
    }

    @Override
    public Optional<Mechanic> getMechanicByNationalId(Integer nationalIdNumber) {
        return mechanicRepository.findMechanicByNationalIdNumber(nationalIdNumber);
    }

    //For system admin
    @Override
    public List<Mechanic> getAllMechanics() {
        return  mechanicRepository.findAll();
    }

    //Get all mechanics for a certain garage
    @Override
    public List<Mechanic> getMechanicsByGarageId(Long garageId) {
        return mechanicRepository.findByGarageId(garageId);
    }

    @Override
    public Mechanic updateMechanic(Long id, Mechanic mechanic){
        return mechanicRepository.findById(id).map(em -> {
            if (mechanic.getAreasofSpecialization() != null) em.setAreasofSpecialization(mechanic.getAreasofSpecialization());
            if (mechanic.getAlternativePhone() != null) em.setAlternativePhone(mechanic.getAlternativePhone());
            if (mechanic.getNationalIdNumber() != null) em.setNationalIdNumber(mechanic.getNationalIdNumber());
            if (mechanic.getPhysicalAddress() != null) em.setPhysicalAddress(mechanic.getPhysicalAddress());
            if (mechanic.getEmergencyContactName() != null) em.setEmergencyContactName(mechanic.getEmergencyContactName());
            if (mechanic.getEmergencyContactNumber() != null) em.setEmergencyContactNumber(mechanic.getEmergencyContactNumber());
            if (mechanic.getYearsofExperience() != null) em.setYearsofExperience(mechanic.getYearsofExperience());
            if (mechanic.getVehicleBrands() != null) em.setVehicleBrands(mechanic.getVehicleBrands());
            if (mechanic.getAvailability() != null) em.setAvailability(mechanic.getAvailability());

            //binary files
            if (mechanic.getProfilePic() != null && mechanic.getProfilePic().length > 0) em.setProfilePic(mechanic.getProfilePic());
            if (mechanic.getNationalIDPic() != null && mechanic.getNationalIDPic().length > 0) em.setNationalIDPic(mechanic.getNationalIDPic());
            if (mechanic.getProfessionalCertfificate() != null && mechanic.getProfessionalCertfificate().length > 0) em.setProfessionalCertfificate(mechanic.getProfessionalCertfificate());
            if (mechanic.getAnyRelevantCertificate() != null && mechanic.getAnyRelevantCertificate().length > 0) em.setAnyRelevantCertificate(mechanic.getAnyRelevantCertificate());
            if (mechanic.getPoliceClearanceCertficate() != null && mechanic.getPoliceClearanceCertficate().length > 0) em.setPoliceClearanceCertficate(mechanic.getPoliceClearanceCertficate());

            //garage the mechanic belongs to
            if (mechanic.getGarage() != null && mechanic.getGarage().getGarageId() != null) {
                Garage garage = garageRepository.findByGarageId(mechanic.getGarage().getGarageId())
                        .orElseThrow(() -> new RuntimeException("Garage not found"));
                em.setGarage(garage);
            }

            return mechanicRepository.save(em);
        }).orElseThrow(()->new RuntimeException("Mechanic not found"));
    }

    @Override
    public String deleteMechanic(Long id) {
        mechanicRepository.deleteById(id);
        return "Mechanic deleted";
    }
}
