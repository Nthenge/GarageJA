package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.service.CarOwnerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carOwner")
public class CarOwnerController {

        CarOwnerService carOwnerService;

        public CarOwnerController(CarOwnerService carOwnerService) {
            this.carOwnerService = carOwnerService;
        }

        @GetMapping("/{carOwnerId}")
        public Optional<CarOwner> getOneCarOwner(@PathVariable("carOwnerId") Long Id){
            return carOwnerService.getCarOwnerById(Id);
        }
        @GetMapping("/search/{carOwnerUniqueId}")
        public Optional<CarOwner> getCarOwnerByUniqueId(@PathVariable("carOwnerUniqueId") Integer carOwnerUniqueId){
            return carOwnerService.getCarOwnerByUniqueId(carOwnerUniqueId);
        }

        @GetMapping()
        public List<CarOwner> getAllCarOwners(){
            return carOwnerService.getAllCarOwners();
        }

        @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> createCarOwner(@RequestBody CarOwner carOwner){
            carOwnerService.createCarOwner(carOwner);
            return ResponseEntity.ok("Car owner created");
        }

        @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<String> uploadCarOwnerProfilePic(
                @RequestParam Integer uniqueId,
                @RequestPart(value = "profilePic", required = false)MultipartFile profilePic) throws java.io.IOException{
            carOwnerService.uploadDocument(uniqueId, profilePic);
            return ResponseEntity.ok("Car owner profile picture uploaded");
        }

        @PutMapping("/{carOwnerId}")
        public String updateCarOwner(@PathVariable Long carOwnerId, @RequestBody CarOwner carOwner){
            carOwnerService.updateCarOwner(carOwnerId, carOwner);
            return "Car Owner updated successfully";
        }

        @DeleteMapping("/{carOwnerId}")
        public String deleteACarOwner(@PathVariable("carOwnerId") Long carOwnerId){
            carOwnerService.deleteCarOwner(carOwnerId);
            return "CarOwner Deleted Successfully";
        }

    }
