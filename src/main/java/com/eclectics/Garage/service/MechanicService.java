package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MechanicService {
    Optional<MechanicResponseDTO> findByUserId(Long userId);

    boolean isDetailsCompleted(Long userId);

    MechanicResponseDTO createMechanic(MechanicRequestDTO mechanicRequestDTO, MultipartFile profilepic, MultipartFile nationalIdFile, MultipartFile profCert, MultipartFile anyRelCert, MultipartFile polCleCert) throws java.io.IOException;
    Optional<MechanicResponseDTO> getMechanicByNationalId(Integer nationalIdNumber);
    List<MechanicResponseDTO> getAllMechanics();
    List<MechanicResponseDTO> getMechanicsByGarageId(Long garageId);
    MechanicResponseDTO updateMechanic(Long id, MechanicRequestDTO mechanic);
    String deleteMechanic(Long id);
}
