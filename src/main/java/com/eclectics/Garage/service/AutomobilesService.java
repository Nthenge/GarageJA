package com.eclectics.Garage.service;

import com.eclectics.Garage.model.AutoMobiles;

import java.util.List;

public interface AutomobilesService {
    AutoMobiles createAutoMobile(AutoMobiles autoMobiles);
    List<AutoMobiles> getAllAutomobiles();
    AutoMobiles updateAutoMobile(Long id, AutoMobiles autoMobiles);
    void deleteAutoMobile(Long id);
    List<String> getAllMakes();
    List<String> findAllTransmission();
    List<String> findAllYears();
    List<String> findAllEngineType();
}
