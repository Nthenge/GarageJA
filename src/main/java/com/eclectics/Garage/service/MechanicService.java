package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MechanicService {
    Optional<MechanicResponseDTO> findByUserId(Long userId);

    boolean isDetailsCompleted(Long userId);

    MechanicResponseDTO createMechanic(MechanicRequestDTO mechanicRequestDTO, MultipartFile profilePic, MultipartFile nationalIDPic, MultipartFile professionalCertfificate, MultipartFile anyRelevantCertificate, MultipartFile policeClearanceCertficate) throws java.io.IOException;
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
