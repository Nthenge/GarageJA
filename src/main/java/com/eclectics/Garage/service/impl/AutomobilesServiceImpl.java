package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.AutoMobiles;
import com.eclectics.Garage.repository.AutomobilesRepository;
import com.eclectics.Garage.service.AutomobilesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomobilesServiceImpl implements AutomobilesService {

    private final AutomobilesRepository automobilesRepository;

    public AutomobilesServiceImpl(AutomobilesRepository automobilesRepository) {
        this.automobilesRepository = automobilesRepository;
    }

    @Override
    public AutoMobiles createAutoMobile(AutoMobiles autoMobiles) {
        return automobilesRepository.save(autoMobiles);
    }

    @Override
    public List<AutoMobiles> getAllAutomobiles() {
        return automobilesRepository.findAll();
    }

    @Override
    public AutoMobiles updateAutoMobile(Long id, AutoMobiles autoMobiles) {
        AutoMobiles existing = automobilesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Automobile not found with id: " + id));

        existing.setMake(autoMobiles.getMake());
        existing.setYear(autoMobiles.getYear());
        existing.setEngineType(autoMobiles.getEngineType());
        existing.setTransmission(autoMobiles.getTransmission());

        return automobilesRepository.save(existing);
    }


    @Override
    public void deleteAutoMobile(Long id) {
        if (!automobilesRepository.existsById(id)){
            throw new RuntimeException("Auto mobile with this id does not exist");
        }
        automobilesRepository.deleteById(id);
    }
}
