package com.eclectics.Garage.controller;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.service.GarageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createGarage(@RequestBody Garage garage){
        garageService.createGarage(garage);
        return "Garage created successfully";
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocuments(
            @RequestParam("garageId") Long garageId,
            @RequestPart(value = "businessLicense", required = true) MultipartFile businessLicense,
            @RequestPart(value = "professionalCertificate", required = true) MultipartFile professionalCertificate,
            @RequestPart(value = "facilityPhotos", required = false) MultipartFile facilityPhotos) throws java.io.IOException{
        garageService.uploadDocument(garageId, businessLicense, professionalCertificate, facilityPhotos);
        return ResponseEntity.ok("Garage documents uploaded successfully");
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

