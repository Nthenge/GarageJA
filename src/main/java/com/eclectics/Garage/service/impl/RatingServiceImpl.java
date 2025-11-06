package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.RatingRequestsDTO;
import com.eclectics.Garage.dto.RatingResponseDTO;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.mapper.RatingMapper;
import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.*;
import com.eclectics.Garage.service.RatingService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RequestServiceRepository requestRepository;
    private final GarageRepository garageRepository;
    private final MechanicRepository mechanicRepository;
    private final RatingMapper mapper;

    private final Set<String> ratedRequests = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, List<Rating>> garageRatingsMap = new ConcurrentHashMap<>();
    private final Map<Long, List<Rating>> mechanicRatingsMap = new ConcurrentHashMap<>();
    private final Map<Long, Double> averageGarageRatings = new ConcurrentHashMap<>();
    private final Map<Long, Double> averageMechanicRatings = new ConcurrentHashMap<>();

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
    public synchronized RatingResponseDTO rateService(Long requestId, RatingRequestsDTO dto) {
        String key = requestId + "-" + dto.getGarageId() + "-" + dto.getMechanicId();
        if (ratedRequests.contains(key)) {
            throw new IllegalStateException("This service request has already been rated by the user.");
        }

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

        ratedRequests.add(key);
        updateGarageRatingsCache(garage.getGarageId(), saved);
        updateMechanicRatingsCache(mechanic.getId(), saved);

        computeGarageAverage(garage.getGarageId());
        computeMechanicAverage(mechanic.getId());

        return mapper.toResponse(saved);
    }

    private void updateGarageRatingsCache(Long garageId, Rating newRating) {
        garageRatingsMap.computeIfAbsent(garageId, k -> new ArrayList<>()).add(newRating);
    }

    private void updateMechanicRatingsCache(Long mechanicId, Rating newRating) {
        mechanicRatingsMap.computeIfAbsent(mechanicId, k -> new ArrayList<>()).add(newRating);
    }

    private void computeGarageAverage(Long garageId) {
        List<Rating> ratings = garageRatingsMap.getOrDefault(garageId, Collections.emptyList());
        if (!ratings.isEmpty()) {
            double avg = ratings.stream()
                    .collect(Collectors.summarizingDouble(Rating::getRatingValue))
                    .getAverage();
            averageGarageRatings.put(garageId, avg);
        }
    }

    private void computeMechanicAverage(Long mechanicId) {
        List<Rating> ratings = mechanicRatingsMap.getOrDefault(mechanicId, Collections.emptyList());
        if (!ratings.isEmpty()) {
            double avg = ratings.stream()
                    .collect(Collectors.summarizingDouble(Rating::getRatingValue))
                    .getAverage();
            averageMechanicRatings.put(mechanicId, avg);
        }
    }

    public double getAverageRatingForGarage(Long garageId) {
        return averageGarageRatings.getOrDefault(garageId, 0.0);
    }

    public double getAverageRatingForMechanic(Long mechanicId) {
        return averageMechanicRatings.getOrDefault(mechanicId, 0.0);
    }

    public List<RatingResponseDTO> getRatingsByMechanic(Long mechanicId) {
        List<Rating> ratings = mechanicRatingsMap.getOrDefault(mechanicId, ratingRepository.findByMechanicId(mechanicId));
        return ratings.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    public List<RatingResponseDTO> getRatingsByGarage(Long garageId) {
        List<Rating> ratings = garageRatingsMap.getOrDefault(garageId, ratingRepository.findByGarageId(garageId));
        return ratings.stream().map(mapper::toResponse).collect(Collectors.toList());
    }
}
