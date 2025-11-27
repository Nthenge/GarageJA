package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.CarOwnerRequestsDTO;
import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.mapper.CarOwnerMapper;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.CarOwnerService;
import org.springframework.http.HttpStatus;
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

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
        @GetMapping("/search")
        public ResponseEntity<Object> filterCarOwners(
                @RequestParam(required = false) String licensePlate,
                @RequestParam(required = false) Integer uniqueId
        ){

            List<CarOwnerResponseDTO> allCarOwners = carOwnerService.filterCarOwners(
                    licensePlate,
                    uniqueId
            );
            return ResponseHandler.generateResponse("Cars fetched successfully", HttpStatus.OK, allCarOwners);
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'CAR_OWNER')")
        @PostMapping(
                value = "/create",
                consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
        )
        public ResponseEntity<Object> createCarOwner(
                @RequestPart("carOwner") CarOwnerRequestsDTO carOwnerRequestsDTO,
                @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
        ) throws IOException {
            carOwnerService.createCarOwner(carOwnerRequestsDTO, profilePic);
            Map<String, String> response = new HashMap<>();
            return ResponseHandler.generateResponse("Car Owner created successfully", HttpStatus.CREATED, response);
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','CAR_OWNER')")
        @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Object> updateOwnProfile(
                @RequestPart("carOwner") CarOwnerRequestsDTO carOwnerRequestsDTO,
                @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
        ) throws IOException {

            CarOwnerResponseDTO updatedOwner = carOwnerService.updateOwnProfile(carOwnerRequestsDTO, profilePic);
            return ResponseHandler.generateResponse("CarOwner profile updated successfully", HttpStatus.CREATED,updatedOwner);
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
            @DeleteMapping("/{carOwnerId}")
            public ResponseEntity<Object> deleteACarOwner(@PathVariable("carOwnerId") Long carOwnerId){
                carOwnerService.deleteCarOwner(carOwnerId);
                return ResponseHandler.generateResponse("CarOwner Deleted Successfully", HttpStatus.OK, null);
            }

        }
