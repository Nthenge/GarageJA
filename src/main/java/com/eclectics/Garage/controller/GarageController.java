package com.eclectics.Garage.controller;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.service.GarageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/garage")
public class GarageController {

    GarageService garageService;

    public GarageController(GarageService garageService) {
        this.garageService = garageService;
    }

    //get garage by id, can be used by mechanic
    @GetMapping("/search/{garageId}")
    public ResponseEntity<Garage> getGarageById(@PathVariable Long garageId){
        Optional<Garage> garage = garageService.getGarageById(garageId);
        if (garage.isPresent()){
            return ResponseEntity.ok(garage.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public List<Garage> getAllGarages(){
        return garageService.getAllGarages();
    }

    @PostMapping()
    public String createGarage(@RequestBody Garage garage){
        garageService.createGarage(garage);
        return "Garage created successfully";
    }

    @PutMapping("/{garageId}")
    public String updateGarage(@PathVariable Long garageId, @RequestBody Garage garage){
        garageService.updateGarage(garageId, garage);
        return "Garage updated successfully";
    }

    @DeleteMapping("/{garageId}")
    public String deleteAGarage(@PathVariable("garageId") Long garageId){
        garageService.deleteGarage(garageId);
        return "Garage Deleted Succesfully";
    }

}

