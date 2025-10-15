package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.AssignMechanicsRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.RequestServiceRepository;
import com.eclectics.Garage.service.AssignMechanicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssingMechanicServiceImpl implements AssignMechanicService {

    private static final Logger logger = LoggerFactory.getLogger(AssingMechanicServiceImpl.class);

    private final AssignMechanicsRepository assignMechanicsRepository;
    private final MechanicRepository mechanicRepository;
    private final RequestServiceRepository requestServiceRepository;

    public AssingMechanicServiceImpl(AssignMechanicsRepository assignMechanicsRepository,
                                     MechanicRepository mechanicRepository,
                                     RequestServiceRepository requestServiceRepository) {
        this.assignMechanicsRepository = assignMechanicsRepository;
        this.mechanicRepository = mechanicRepository;
        this.requestServiceRepository = requestServiceRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allAssignments", allEntries = true),
            @CacheEvict(value = "assignmentsByMechanic", allEntries = true),
            @CacheEvict(value = "assignmentsByRequest", allEntries = true)
    })
    public AssignMechanics assignRequestToMechanic(Long requestId, Long mechanicId) {
        logger.info("Assigning service request ID {} to mechanic ID {}", requestId, mechanicId);

        ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error("ServiceRequest with ID {} not found", requestId);
                    return new RuntimeException("Service with this id does not exist");
                });

        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(() -> {
                    logger.error("Mechanic with ID {} not found", mechanicId);
                    return new RuntimeException("Mechanic with this id does not exist");
                });

        AssignMechanics assignRequests = new AssignMechanics();
        assignRequests.setService(serviceRequest);
        assignRequests.setMechanic(mechanic);
        assignRequests.setStatus(AssignMechanicStatus.ASSINGED);
        assignRequests.setAssignedAt(LocalDateTime.now());
        assignRequests.setUpdatedAt(LocalDateTime.now());

        AssignMechanics savedAssignment = assignMechanicsRepository.save(assignRequests);
        logger.info("Successfully assigned mechanic ID {} to service request ID {}", mechanicId, requestId);
        return savedAssignment;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allAssignments", allEntries = true),
            @CacheEvict(value = "assignmentsByMechanic", allEntries = true),
            @CacheEvict(value = "assignmentsByRequest", allEntries = true)
    })
    public AssignMechanics updateAssignmentStatus(Long assignmentId, AssignMechanicStatus status) {
        logger.info("Updating assignment ID {} to status {}", assignmentId, status);

        AssignMechanics assignRequests = assignMechanicsRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    logger.error("Assignment with ID {} not found", assignmentId);
                    return new RuntimeException("Request with this id does not exist");
                });

        assignRequests.setStatus(status);
        assignRequests.setUpdatedAt(LocalDateTime.now());

        AssignMechanics updatedAssignment = assignMechanicsRepository.save(assignRequests);
        logger.info("Assignment ID {} updated to status {}", assignmentId, status);
        return updatedAssignment;
    }

    @Override
    @Cacheable(value = "assignmentsByMechanic", key = "#mechanicId")
    public List<AssignMechanics> getAssignmentsByMechanic(Long mechanicId) {
        logger.info("Fetching assignments for mechanic ID {}", mechanicId);
        List<AssignMechanics> assignments = assignMechanicsRepository.findByMechanicId(mechanicId);
        logger.debug("Found {} assignments for mechanic ID {}", assignments.size(), mechanicId);
        return assignments;
    }

    @Override
    @Cacheable(value = "assignmentsByRequest", key = "#requestId")
    public List<AssignMechanics> getAssignmentByRequest(Long requestId) {
        logger.info("Fetching assignments for service request ID {}", requestId);
        List<AssignMechanics> assignments = assignMechanicsRepository.findByService_Id(requestId);
        logger.debug("Found {} assignments for request ID {}", assignments.size(), requestId);
        return assignments;
    }

    @Override
    @Cacheable(value = "allAssignments")
    public List<AssignMechanics> getAllAssignments() {
        logger.info("Fetching all mechanic assignments");
        List<AssignMechanics> assignments = assignMechanicsRepository.findAll();
        logger.debug("Total assignments found: {}", assignments.size());
        return assignments;
    }
}
