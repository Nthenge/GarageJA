package com.eclectics.Garage.service;

import com.eclectics.Garage.model.Mechanic;
import io.jsonwebtoken.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MechanicService {
    Mechanic createMechanic(Mechanic mechanic);
    Mechanic uploadDocuments(Long id, MultipartFile profilepic, MultipartFile nationalIdFile, MultipartFile profCert, MultipartFile anyRelCert, MultipartFile polCleCert) throws java.io.IOException;
    Optional<Mechanic> getMechanicByNationalId(Integer nationalIdNumber);
    List<Mechanic> getAllMechanics();
    List<Mechanic> getMechanicsByGarageId(Long garageId);
    Mechanic updateMechanic(Long id, Mechanic mechanic);
    String deleteMechanic(Long id);
}
