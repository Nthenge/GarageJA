package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.AutoMobileResponseDTO;
import com.eclectics.Garage.dto.AutomobileRequestsDTO;

import java.util.List;

public interface AutomobilesService {
    AutoMobileResponseDTO createAutoMobile(AutomobileRequestsDTO automobileRequestsDTO);
    List<AutoMobileResponseDTO> getAllAutomobiles();
    AutoMobileResponseDTO updateAutoMobile(Long id, AutomobileRequestsDTO automobileRequestsDTO);
    void deleteAutoMobile(Long id);
    List<String> getAllMakes();
    List<String> findAllTransmission();
    List<String> findAllYears();
    List<String> findAllEngineType();
}
