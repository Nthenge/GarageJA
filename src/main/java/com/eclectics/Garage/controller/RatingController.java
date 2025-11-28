package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.RatingRequestsDTO;
import com.eclectics.Garage.dto.RatingResponseDTO;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @PostMapping("/rating/{id}")
    public ResponseEntity<Object> rateRequest(
            @PathVariable Long id,
            @RequestBody RatingRequestsDTO dto) {

        RatingResponseDTO response = ratingService.rateService(id, dto);
        return ResponseHandler.generateResponse("Request rating", HttpStatus.CREATED, response, "/rating/rating/{id}");
    }
}
