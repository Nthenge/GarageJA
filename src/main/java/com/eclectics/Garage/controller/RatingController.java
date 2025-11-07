package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.RatingRequestsDTO;
import com.eclectics.Garage.dto.RatingResponseDTO;
import com.eclectics.Garage.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/rating/{id}")
    public ResponseEntity<RatingResponseDTO> rateRequest(
            @PathVariable Long id,
            @RequestBody RatingRequestsDTO dto) {

        RatingResponseDTO response = ratingService.rateService(id, dto);
        return ResponseEntity.ok(response);
    }
}
