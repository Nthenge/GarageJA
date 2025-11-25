package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.MechanicGarageRegisterRequestDTO;
import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MechanicService {
    Optional<MechanicResponseDTO> findByUserId(Long userId);

    boolean isDetailsCompleted(Long userId);

    @Transactional
    User registerMechanic(MechanicGarageRegisterRequestDTO dto);
    Optional<MechanicResponseDTO> getMechanicByNationalId(Integer nationalIdNumber);
    List<MechanicResponseDTO> getAllMechanics();
    List<MechanicResponseDTO> getMechanicsByGarageId(Long garageId);
    MechanicResponseDTO updateOwnMechanic(MechanicRequestDTO mechanicRequestDTO,
                                       MultipartFile profilePic,
                                       MultipartFile nationalIDPic,
                                       MultipartFile professionalCertificate,
                                       MultipartFile anyRelevantCertificate,
                                       MultipartFile policeClearanceCertificate);
    String deleteMechanic(Long id);
    Optional<String> getMechanicFilesUrlByNationalId(Integer nationalId, int expiryMinutes);
}
