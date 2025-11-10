package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.mapper.CarOwnerMapper;
import com.eclectics.Garage.service.CarOwnerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER')")
        @GetMapping("/search/{carOwnerUniqueId}")
        public Optional<CarOwnerResponseDTO> getCarOwnerByUniqueId(@PathVariable("carOwnerUniqueId") Integer carOwnerUniqueId){
            return carOwnerService.getCarOwnerByUniqueId(carOwnerUniqueId);
        }

        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @GetMapping()
        public List<CarOwnerResponseDTO> getAllCarOwners(){
            return carOwnerService.getAllCarOwners();
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'CAR_OWNER')")
        @PostMapping(
                value = "/create",
                consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
        )
        public ResponseEntity<Map<String, String>> createCarOwner(
                @RequestPart("carOwner") CarOwnerRequestsDTO carOwnerRequestsDTO,
                @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
        ) throws IOException {
            carOwnerService.createCarOwner(carOwnerRequestsDTO, profilePic);
            Map<String, String> response = new HashMap<>();
            return ResponseEntity.ok(response);
        }

        @PreAuthorize("hasRole('CAR_OWNER')")
        @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<CarOwnerResponseDTO> updateOwnProfile(
                @RequestPart("carOwner") CarOwnerRequestsDTO carOwnerRequestsDTO,
                @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
        ) throws IOException {

            CarOwnerResponseDTO updatedOwner = carOwnerService.updateOwnProfile(carOwnerRequestsDTO, profilePic);
            return ResponseEntity.ok(updatedOwner);
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
            @DeleteMapping("/{carOwnerId}")
            public String deleteACarOwner(@PathVariable("carOwnerId") Long carOwnerId){
                carOwnerService.deleteCarOwner(carOwnerId);
                return "CarOwner Deleted Successfully";
            }

        }
