package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.RatingRequestsDTO;
import com.eclectics.Garage.dto.RatingResponseDTO;

public interface RatingService {
    RatingResponseDTO rateService(Long requestId, RatingRequestsDTO ratingRequestDTO);
}
