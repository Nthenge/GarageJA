package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.AutoMobiles;
import com.eclectics.Garage.service.AutomobilesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/automobiles")
public class AutoMobilesController {

    AutomobilesService automobilesService;

    public AutoMobilesController(AutomobilesService automobilesService) {
        this.automobilesService = automobilesService;
    }

    @GetMapping
    public List<AutoMobiles> getAllAutomobiles(){
        return automobilesService.getAllAutomobiles();
    }
    @GetMapping("/make")
    public List<String> getAllMakes() {
        return automobilesService.getAllMakes();
    }
    @GetMapping("/year")
    public List<String> getAllYea() {
        return automobilesService.findAllYears();
    }
    @GetMapping("/engineType")
    public List<String> getAllEngineType() {
        return automobilesService.findAllEngineType();
    }
    @GetMapping("/transmission")
    public List<String> getAllTransmission() {
        return automobilesService.findAllTransmission();
    }

    @PostMapping
    public AutoMobiles createAutoMobile(@RequestBody AutoMobiles autoMobiles){
        return automobilesService.createAutoMobile(autoMobiles);
    }

    @PutMapping("/{id}")
    public AutoMobiles updateAutoMobile(@PathVariable Long id, @RequestBody AutoMobiles autoMobiles){
        return automobilesService.updateAutoMobile(id, autoMobiles);
    }

    @DeleteMapping("/{id}")
    public void deleteAutoMobile(@PathVariable Long id){
        automobilesService.deleteAutoMobile(id);
    }
}
