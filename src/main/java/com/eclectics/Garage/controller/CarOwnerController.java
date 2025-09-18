package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.service.CarOwnerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carOwner")
@CrossOrigin(origins = "http://10.20.33.84:4200",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
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

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createCarOwner(
            @RequestPart("carOwner") CarOwner carOwner,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
    ) throws IOException {
        carOwnerService.createCarOwner(carOwner);
        return ResponseEntity.ok("Car owner created with profile picture");
    }


//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//        public ResponseEntity<String> uploadCarOwnerProfilePic(
//                @RequestParam Integer uniqueId,
//                @RequestPart(value = "profilePic", required = false)MultipartFile profilePic) throws java.io.IOException{
//            carOwnerService.uploadDocument(uniqueId, profilePic);
//            return ResponseEntity.ok("Car owner profile picture uploaded");
//        }

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
