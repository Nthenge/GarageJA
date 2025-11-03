package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.RatingRequestsDTO;
import com.eclectics.Garage.dto.RatingResponseDTO;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.mapper.RatingMapper;
import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.*;
import com.eclectics.Garage.service.RatingService;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RequestServiceRepository requestRepository;
    private final GarageRepository garageRepository;
    private final MechanicRepository mechanicRepository;
    private final RatingMapper mapper;

    public RatingServiceImpl(RatingRepository ratingRepository,
                             RequestServiceRepository requestRepository,
                             GarageRepository garageRepository,
                             MechanicRepository mechanicRepository,
                             RatingMapper mapper) {
        this.ratingRepository = ratingRepository;
        this.requestRepository = requestRepository;
        this.garageRepository = garageRepository;
        this.mechanicRepository = mechanicRepository;
        this.mapper = mapper;
    }

    @Override
    public RatingResponseDTO rateService(Long requestId, RatingRequestsDTO dto) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Request not found"));

        Garage garage = garageRepository.findByGarageId(dto.getGarageId())
                .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));

        Mechanic mechanic = mechanicRepository.findById(dto.getMechanicId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        Rating rating = mapper.toEntity(dto);
        rating.setServiceRequest(request);
        rating.setGarage(garage);
        rating.setMechanic(mechanic);

        Rating saved = ratingRepository.save(rating);
        return mapper.toResponse(saved);
    }
}
