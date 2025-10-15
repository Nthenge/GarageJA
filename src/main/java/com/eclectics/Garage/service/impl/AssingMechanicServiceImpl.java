package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.AssignMechanicsRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.RequestServiceRepository;
import com.eclectics.Garage.service.AssignMechanicService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssingMechanicServiceImpl implements AssignMechanicService {

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
        ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service with this id does not exist"));

        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic with this id does not exist"));

        AssignMechanics assignRequests = new AssignMechanics();
        assignRequests.setService(serviceRequest);
        assignRequests.setMechanic(mechanic);
        assignRequests.setStatus(AssignMechanicStatus.ASSINGED);
        assignRequests.setAssignedAt(LocalDateTime.now());
        assignRequests.setUpdatedAt(LocalDateTime.now());
        return assignMechanicsRepository.save(assignRequests);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allAssignments", allEntries = true),
            @CacheEvict(value = "assignmentsByMechanic", allEntries = true),
            @CacheEvict(value = "assignmentsByRequest", allEntries = true)
    })
    public AssignMechanics updateAssignmentStatus(Long assignmentId, AssignMechanicStatus status) {
        AssignMechanics assignRequests = assignMechanicsRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Request with this id does not exist"));

        assignRequests.setStatus(status);
        assignRequests.setUpdatedAt(LocalDateTime.now());
        return assignMechanicsRepository.save(assignRequests);
    }

    @Override
    @Cacheable(value = "assignmentsByMechanic", key = "#mechanicId")
    public List<AssignMechanics> getAssignmentsByMechanic(Long mechanicId) {
        return assignMechanicsRepository.findByMechanicId(mechanicId);
    }

    @Override
    @Cacheable(value = "assignmentsByRequest", key = "#requestId")
    public List<AssignMechanics> getAssignmentByRequest(Long requestId) {
        return assignMechanicsRepository.findByService_Id(requestId);
    }

    @Override
    @Cacheable(value = "allAssignments")
    public List<AssignMechanics> getAllAssignments() {
        return assignMechanicsRepository.findAll();
    }
}
