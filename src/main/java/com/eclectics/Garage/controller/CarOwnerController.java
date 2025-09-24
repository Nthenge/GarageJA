package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.mapper.CarOwnerMapper;
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
public class CarOwnerController {

        private final CarOwnerService carOwnerService;
        private final CarOwnerMapper mapper;

        public CarOwnerController(CarOwnerService carOwnerService, CarOwnerMapper mapper) {
            this.carOwnerService = carOwnerService;
            this.mapper = mapper;
        }

        @GetMapping("/search/{carOwnerUniqueId}")
        public Optional<CarOwnerResponseDTO> getCarOwnerByUniqueId(@PathVariable("carOwnerUniqueId") Integer carOwnerUniqueId){
            return carOwnerService.getCarOwnerByUniqueId(carOwnerUniqueId);
        }

        @GetMapping()
        public List<CarOwnerResponseDTO> getAllCarOwners(){
            return carOwnerService.getAllCarOwners();
        }

        @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
        public ResponseEntity<String> createCarOwner(
                @RequestPart("carOwner") CarOwnerRequestsDTO carOwnerRequestsDTO,
                @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
        ) throws IOException {
            carOwnerService.createCarOwner(carOwnerRequestsDTO, profilePic);
            return ResponseEntity.ok("Car owner created with profile picture");
        }

        @PutMapping(value = "/{carOwnerUniqueId}/uploadprofile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<CarOwnerResponseDTO> updateCarOwnerProfilePic(
                @PathVariable Integer carOwnerUniqueId,
                @RequestPart("profilePic") MultipartFile profilePic
        ) throws IOException {

            CarOwnerResponseDTO updatedOwner = carOwnerService.updateProfilePic(carOwnerUniqueId, profilePic);
            return ResponseEntity.ok(updatedOwner);
        }

        @PutMapping("/{carOwnerId}")
        public ResponseEntity<CarOwnerResponseDTO> updateCarOwner(@PathVariable Long carOwnerId, @RequestBody CarOwnerRequestsDTO carOwnerRequestsDTO){
            CarOwnerResponseDTO updatedOwner = carOwnerService.updateCarOwner(carOwnerId, carOwnerRequestsDTO);
            return ResponseEntity.ok(updatedOwner);
        }

        @DeleteMapping("/{carOwnerId}")
        public String deleteACarOwner(@PathVariable("carOwnerId") Long carOwnerId){
            carOwnerService.deleteCarOwner(carOwnerId);
            return "CarOwner Deleted Successfully";
        }

    }
